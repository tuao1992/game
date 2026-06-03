package com.weldrite.cpvcmaster.screens

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.weldrite.cpvcmaster.audio.Sfx
import com.weldrite.cpvcmaster.data.*
import com.weldrite.cpvcmaster.engine.*
import com.weldrite.cpvcmaster.gfx.Decor
import com.weldrite.cpvcmaster.gfx.PipeRenderer
import com.weldrite.cpvcmaster.ui.BtnStyle
import com.weldrite.cpvcmaster.ui.Button
import com.weldrite.cpvcmaster.ui.Palette
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * The core gameplay: a state machine over the six-step joining loop, shared by
 * Career, Time Attack and Endless modes.
 */
class PlayScreen(game: Game, val mode: GameMode, val levelIndex: Int = -1) : Screen(game) {

    private enum class Phase { SELECT_PIPE, CUT, CLEAN, DEBUR, CEMENT, FIT_SELECT, ALIGN, JOIN, TEST }

    private val rng = Random(System.nanoTime())
    private val level: Level? = if (mode == GameMode.CAREER && levelIndex >= 0) Levels.all[levelIndex] else null
    private val particles = Particles().apply { densityScale = game.save.quality.particleScale }

    // Session state
    private var phase = Phase.SELECT_PIPE
    private var cfg: JointConfig = newJointConfig()
    private var jointsDone = 0
    private var jointsTarget = level?.jointCount ?: Int.MAX_VALUE
    private var jointIndex = 0
    private var lives = if (mode == GameMode.ENDLESS) 3 else 0
    private var timeLeft = when (mode) {
        GameMode.CAREER -> level?.timeLimit ?: 0f
        GameMode.TIME_ATTACK -> 60f
        else -> 0f
    }
    private var totalScore = 0
    private var levelQualitySum = 0f
    private var anyMajorLeak = false
    private var anyLeak = false

    // Per-joint sub-scores (0..1)
    private var sSel = 1f; private var sCut = 0f; private var sClean = 0f; private var sDebur = 0f
    private var sCement = 0f; private var sFsel = 1f; private var sAlign = 0f; private var sHold = 0f

    private var phaseT = 0f
    private var instruction = ""
    private var flash = 0f

    // Overlays
    private var paused = false
    private var ended = false
    private var endStars = 0
    private var endFailed = false
    private var newBest = false
    private var unlockedRank: Rank? = null

    // Geometry (computed per joint)
    private var pipeLeft = 0f; private var pipeCY = 0f; private var pipeLen = 0f; private var tubeR = 0f
    private var fitX = 0f

    // Pointer
    private var ptrDown = false; private var ptrX = 0f; private var ptrY = 0f; private var lastX = 0f; private var lastY = 0f

    // Buttons
    private val pauseBtn = Button("pause").apply { style = BtnStyle.GHOST; label = "II"; textScale = 0.5f }
    private val cta = Button("cta").apply { style = BtnStyle.PRIMARY }
    private val resumeB = Button("resume").apply { style = BtnStyle.PRIMARY }
    private val restartB = Button("restart").apply { style = BtnStyle.SECONDARY }
    private val exitB = Button("exit").apply { style = BtnStyle.DANGER }
    private val nextB = Button("next").apply { style = BtnStyle.SUCCESS }
    private val retryB = Button("retry").apply { style = BtnStyle.SECONDARY }
    private val menuB = Button("menu").apply { style = BtnStyle.GHOST }
    private var armed: Button? = null

    // Option tiles (pipe / fitting selection)
    private val optRects = ArrayList<RectF>()

    // Phase-specific state
    private var cutMarker = 0.1f; private var cutDir = 1f; private var cutSpeed = 1f; private var cutDone = false
    private val cleanSeg = FloatArray(12); private var cleanCover = 0f
    private val burrs = ArrayList<Float>(); private var burrTimer = 0f
    private val cementBuckets = BooleanArray(14); private var cementCover = 0f; private var cementAmount = 0f
    private var fitAngle = 0f; private var fitTarget = 0f
    private var holdProgress = 0f; private var holdSteady = 1f; private var holdElapsed = 0f; private var holdInTime = 0f
    private var waterFill = 0f; private var result = JointResult.PERFECT; private var testComputed = false; private var testHold = 0f
    private var cutHoldT = 0f
    private var endT = 0f

    // Achievement toast queue
    private val achQueue = ArrayDeque<Achievement>()
    private var achT = 0f

    private val hudButtons = listOf(pauseBtn, cta, resumeB, restartB, exitB, nextB, retryB, menuB)

    init { enterPhase(Phase.SELECT_PIPE) }

    private fun newJointConfig(): JointConfig = when (mode) {
        GameMode.CAREER -> level!!.makeJoint(rng)
        GameMode.TIME_ATTACK -> Levels.timeAttackJoint(rng)
        else -> Levels.endlessJoint(jointIndex, rng)
    }

    override fun layout() {
        pauseBtn.set(w - p.dp(96f), p.dp(28f), w - p.dp(24f), p.dp(100f))
        computeGeom()
        layoutOverlayButtons()
        layoutCta()
        layoutOptions()
    }

    private fun computeGeom() {
        tubeR = w * 0.045f * (1f + cfg.pipe.relDia)
        pipeCY = h * 0.52f
        pipeLeft = w * 0.12f
        pipeLen = w * 0.46f
        fitX = w * 0.76f
    }

    private fun layoutOverlayButtons() {
        val cx = w / 2f; val bw = w * 0.62f; val bh = p.dp(110f); val gap = p.dp(24f)
        var y = h * 0.40f
        for (b in listOf(resumeB, restartB, exitB)) { b.set(cx - bw / 2, y, cx + bw / 2, y + bh); y += bh + gap }
        // result buttons row
        y = h * 0.62f
        val half = (bw - gap) / 2f
        nextB.set(cx - bw / 2, y, cx + bw / 2, y + bh)
        retryB.set(cx - bw / 2, y + bh + gap, cx - bw / 2 + half, y + bh + gap + bh * 0.9f)
        menuB.set(cx + bw / 2 - half, y + bh + gap, cx + bw / 2, y + bh + gap + bh * 0.9f)
    }

    private fun layoutCta() {
        cta.set(w / 2f - p.dp(180f), h * 0.86f, w / 2f + p.dp(180f), h * 0.86f + p.dp(100f))
    }

    private fun layoutOptions() {
        optRects.clear()
        val n = when (phase) { Phase.SELECT_PIPE -> cfg.pipeOptions.size; Phase.FIT_SELECT -> cfg.fittingOptions.size; else -> 0 }
        if (n == 0) return
        val cols = if (n <= 3) n else (n + 1) / 2
        val rows = (n + cols - 1) / cols
        val gap = p.dp(22f)
        val areaT = h * 0.6f; val areaB = h * 0.92f
        val tileW = (w * 0.86f - gap * (cols - 1)) / cols
        val tileH = ((areaB - areaT) - gap * (rows - 1)) / rows
        val startX = w * 0.07f
        for (i in 0 until n) {
            val col = i % cols; val row = i / cols
            val x = startX + col * (tileW + gap)
            val y = areaT + row * (tileH + gap)
            optRects.add(RectF(x, y, x + tileW, y + tileH))
        }
    }

    // ---------- Phase management ----------
    private fun enterPhase(ph: Phase) {
        phase = ph; phaseT = 0f
        instruction = when (ph) {
            Phase.SELECT_PIPE -> Loc.t("select_pipe")
            Phase.CUT -> Loc.t("cut_pipe")
            Phase.CLEAN -> Loc.t("clean_pipe")
            Phase.DEBUR -> Loc.t("debur_pipe")
            Phase.CEMENT -> Loc.t("apply_cement")
            Phase.FIT_SELECT -> Loc.t("align_fitting")
            Phase.ALIGN -> Loc.t("align_fitting")
            Phase.JOIN -> Loc.t("join_pipe")
            Phase.TEST -> Loc.t("pressure_test")
        }
        when (ph) {
            Phase.CUT -> { cutMarker = 0.08f; cutDir = 1f; cutSpeed = Geom.lerp(0.55f, 1.4f, cfg.difficulty); cutDone = false; cutHoldT = 0f }
            Phase.CLEAN -> { cleanSeg.fill(0f); cleanCover = 0f }
            Phase.DEBUR -> {
                burrs.clear(); val count = (3 + cfg.difficulty * 3).toInt().coerceIn(3, 6)
                repeat(count) { burrs.add(Geom.lerp(-1.1f, 1.1f, rng.nextFloat())) }
                burrTimer = 0f
            }
            Phase.CEMENT -> { cementBuckets.fill(false); cementCover = 0f; cementAmount = 0f }
            Phase.ALIGN -> { fitTarget = 0f; fitAngle = (if (rng.nextBoolean()) 1 else -1) * Geom.lerp(35f, 80f, rng.nextFloat()) }
            Phase.JOIN -> { holdProgress = 0f; holdSteady = 1f; holdElapsed = 0f; holdInTime = 0f }
            Phase.TEST -> { waterFill = 0f; testComputed = false; testHold = 0f }
            else -> {}
        }
        layoutOptions(); layoutCta()
    }

    private fun nextPhase() {
        val order = Phase.entries
        val i = order.indexOf(phase)
        if (i < order.lastIndex) enterPhase(order[i + 1]) else afterTest()
    }

    // ---------- Update ----------
    override fun update(dt: Float) {
        particles.update(dt)
        flash = (flash - dt * 3f).coerceAtLeast(0f)
        for (b in hudButtons) b.update(dt)
        if (achQueue.isNotEmpty()) { achT += dt; if (achT > 2.8f) { achQueue.removeFirst(); achT = 0f } }
        if (ended) { endT += dt; return }
        if (paused) return

        phaseT += dt
        if (timeLeft > 0f && mode != GameMode.ENDLESS) {
            timeLeft -= dt
            if (timeLeft <= 0f) { timeLeft = 0f; onTimeUp(); return }
        }

        when (phase) {
            Phase.CUT -> {
                if (!cutDone) {
                    cutMarker += cutDir * cutSpeed * dt
                    if (cutMarker > 0.92f) { cutMarker = 0.92f; cutDir = -1f }
                    if (cutMarker < 0.08f) { cutMarker = 0.08f; cutDir = 1f }
                } else { cutHoldT += dt; if (cutHoldT > 0.55f) nextPhase() }
            }
            Phase.DEBUR -> burrTimer += dt
            Phase.JOIN -> updateHold(dt)
            Phase.TEST -> updateTest(dt)
            else -> {}
        }
    }

    private fun updateHold(dt: Float) {
        val tx = fitX - tubeR * 0.4f; val ty = pipeCY
        val inZone = ptrDown && Geom.dist(ptrX, ptrY, tx, ty) < tubeR * 3.0f
        val holdDur = Geom.lerp(1.8f, 2.6f, cfg.difficulty)
        if (ptrDown) {
            holdElapsed += dt
            if (inZone) { holdProgress += dt / holdDur; holdInTime += dt }
            else holdProgress -= dt * 0.4f
            holdProgress = holdProgress.coerceIn(0f, 1f)
            if (holdElapsed > 0.05f) holdSteady = (holdInTime / holdElapsed).coerceIn(0f, 1f)
            if ((phaseT * 12).toInt() % 3 == 0 && inZone) particles.spray(tx, ty, -1.5708f, 2.5f, 1, PipeRenderer.CREAM, 120f, tubeR * 0.18f, 400f, 0.4f)
            if (holdProgress >= 1f) {
                sHold = (0.4f + 0.6f * holdSteady).coerceIn(0f, 1f)
                game.audio.play(Sfx.JOIN_SET); game.haptics.heavy()
                nextPhase()
            }
        } else {
            holdProgress -= dt * 0.6f
            holdProgress = holdProgress.coerceAtLeast(0f)
        }
    }

    private fun updateTest(dt: Float) {
        if (!testComputed) { computeResult(); testComputed = true; game.audio.play(Sfx.WATER) }
        waterFill = (waterFill + dt * 0.8f).coerceAtMost(1f)
        if (waterFill >= 1f) {
            testHold += dt
            if (testHold > 1.8f) afterTest()
        }
    }

    // ---------- Touch ----------
    override fun onTouch(e: TouchEvent) {
        when (e.kind) {
            TouchEvent.Kind.DOWN -> { ptrDown = true; ptrX = e.x; ptrY = e.y; lastX = e.x; lastY = e.y }
            TouchEvent.Kind.MOVE -> { ptrX = e.x; ptrY = e.y }
            TouchEvent.Kind.UP -> { ptrDown = false }
            else -> {}
        }

        if (ended) { handleEndButtons(e); afterRoute(e); return }
        if (paused) { handlePauseButtons(e); afterRoute(e); return }

        tap(pauseBtn, e)
        if (pauseBtnFired) { pauseBtnFired = false; openPause(); afterRoute(e); return }

        phaseTouch(e)
        afterRoute(e)
    }

    private var pauseBtnFired = false
    private fun afterRoute(e: TouchEvent) { if (e.kind == TouchEvent.Kind.MOVE) { lastX = e.x; lastY = e.y } }

    private fun phaseTouch(e: TouchEvent) {
        when (phase) {
            Phase.SELECT_PIPE -> if (e.kind == TouchEvent.Kind.UP) {
                val i = optHit(e); if (i >= 0) {
                    if (cfg.pipeOptions[i] == cfg.pipe) { game.audio.play(Sfx.SELECT); game.haptics.light(); nextPhase() }
                    else { wrong() }
                }
            }
            Phase.CUT -> if (e.kind == TouchEvent.Kind.DOWN && !cutDone) doCut()
            Phase.CLEAN -> if (e.kind != TouchEvent.Kind.UP) paintClean(e)
            Phase.DEBUR -> if (e.kind == TouchEvent.Kind.DOWN) tapBurr(e)
            Phase.CEMENT -> if (e.kind != TouchEvent.Kind.UP) paintCement(e)
            Phase.FIT_SELECT -> if (e.kind == TouchEvent.Kind.UP) {
                val i = optHit(e); if (i >= 0) {
                    if (cfg.fittingOptions[i] == cfg.fitting) { game.audio.play(Sfx.SELECT); game.haptics.light(); nextPhase() }
                    else wrong()
                }
            }
            Phase.ALIGN -> {
                if (e.kind == TouchEvent.Kind.MOVE) fitAngle = (fitAngle + (e.x - lastX) * 0.35f).coerceIn(-130f, 130f)
                tap(cta, e)
                if (ctaFired) { ctaFired = false; confirmAlign() }
            }
            Phase.TEST -> if (e.kind == TouchEvent.Kind.UP && waterFill >= 1f) afterTest()
            else -> {}
        }
    }

    private var ctaFired = false

    private fun doCut() {
        cutDone = true
        val off = abs(cutMarker - 0.5f) / 0.42f
        sCut = Geom.clamp01(1f - off * off)
        game.audio.play(Sfx.CUT); game.haptics.medium()
        particles.spray(pipeLeft + pipeLen * cutMarker, pipeCY, 1.5708f, 1.6f, 14, 0xFFD9C9A0.toInt(), 360f, tubeR * 0.22f, 700f, 0.6f)
        particles.spray(pipeLeft + pipeLen * cutMarker, pipeCY, -1.5708f, 1.6f, 8, 0xFFD9C9A0.toInt(), 320f, tubeR * 0.2f, 700f, 0.6f)
    }

    private fun paintClean(e: TouchEvent) {
        if (e.y < pipeCY - tubeR * 2.2f || e.y > pipeCY + tubeR * 2.2f) return
        val rel = (e.x - pipeLeft) / pipeLen
        if (rel < 0f || rel > 1f) return
        val idx = (rel * cleanSeg.size).toInt().coerceIn(0, cleanSeg.lastIndex)
        if (cleanSeg[idx] < 1f) {
            cleanSeg[idx] = 1f
            if (idx > 0) cleanSeg[idx - 1] = maxOf(cleanSeg[idx - 1], 0.7f)
            if (idx < cleanSeg.lastIndex) cleanSeg[idx + 1] = maxOf(cleanSeg[idx + 1], 0.7f)
            if (rng.nextFloat() < 0.4f) { game.audio.play(Sfx.CLEAN); game.haptics.tick() }
            particles.burst(e.x, pipeCY - tubeR, 2, 0xFFBCA77A.toInt(), 120f, tubeR * 0.14f, 500f, 0.4f)
        }
        cleanCover = cleanSeg.average().toFloat()
        if (cleanCover >= 0.93f) { sClean = cleanCover; game.haptics.light(); nextPhase() }
    }

    private fun tapBurr(e: TouchEvent) {
        val ex = fitX.let { pipeLeft + pipeLen } // right end
        var hit = -1
        for (i in burrs.indices) {
            val bx = pipeLeft + pipeLen + tubeR * 0.1f
            val by = pipeCY + burrs[i] * tubeR
            if (Geom.dist(e.x, e.y, bx, by) < tubeR * 0.7f) { hit = i; break }
        }
        if (hit >= 0) {
            val by = pipeCY + burrs[hit] * tubeR
            burrs.removeAt(hit)
            game.audio.play(Sfx.DEBUR); game.haptics.light()
            particles.burst(pipeLeft + pipeLen, by, 6, 0xFFAEB7C2.toInt(), 200f, tubeR * 0.16f, 600f, 0.5f)
            if (burrs.isEmpty()) { sDebur = (1f - (burrTimer / 12f)).coerceIn(0.7f, 1f); nextPhase() }
        }
    }

    private fun paintCement(e: TouchEvent) {
        val coatL = pipeLeft + pipeLen * 0.62f; val coatR = pipeLeft + pipeLen
        if (e.y < pipeCY - tubeR * 2f || e.y > pipeCY + tubeR * 2f) return
        if (e.x < coatL - tubeR || e.x > coatR + tubeR) return
        val rel = ((e.x - coatL) / (coatR - coatL)).coerceIn(0f, 1f)
        val idx = (rel * (cementBuckets.size - 1)).toInt().coerceIn(0, cementBuckets.lastIndex)
        if (!cementBuckets[idx]) cementBuckets[idx] = true
        val moved = abs(e.x - lastX) + abs(e.y - lastY)
        cementAmount += moved / (coatR - coatL)
        if (rng.nextFloat() < 0.25f) { game.audio.play(Sfx.CEMENT); game.haptics.tick() }
        particles.burst(e.x, e.y, 1, 0xCC8FCAFF.toInt(), 80f, tubeR * 0.12f, 200f, 0.35f)
        cementCover = cementBuckets.count { it }.toFloat() / cementBuckets.size
        if (cementCover >= 0.95f) {
            val over = (cementAmount - 2.4f).coerceAtLeast(0f)
            val overPen = (over * 0.12f).coerceAtMost(0.3f)
            sCement = (cementCover * (1f - overPen)).coerceIn(0f, 1f)
            game.haptics.light(); nextPhase()
        }
    }

    private fun confirmAlign() {
        val err = abs(fitAngle - fitTarget)
        sAlign = Geom.clamp01(1f - err / 45f)
        if (err < Geom.lerp(14f, 7f, cfg.difficulty)) { fitAngle = fitTarget; game.audio.play(Sfx.ALIGN_LOCK); game.haptics.success() }
        else { game.audio.play(Sfx.SELECT); game.haptics.light() }
        nextPhase()
    }

    private fun wrong() {
        flash = 1f; game.audio.play(Sfx.ERROR); game.haptics.error()
        if (phase == Phase.SELECT_PIPE) sSel = (sSel - 0.35f).coerceAtLeast(0.2f)
        if (phase == Phase.FIT_SELECT) sFsel = (sFsel - 0.35f).coerceAtLeast(0.2f)
    }

    // ---------- Scoring / results ----------
    private fun computeResult() {
        val q = 0.22f * sCement + 0.18f * sAlign + 0.18f * sHold + 0.14f * sCut +
            0.10f * sClean + 0.08f * sDebur + 0.05f * sSel + 0.05f * sFsel
        result = when {
            q >= 0.8f -> JointResult.PERFECT
            q >= 0.55f -> JointResult.MINOR_LEAK
            else -> JointResult.MAJOR_LEAK
        }
        levelQualitySum += q
        val pts = (q * 100).toInt() + when (result) { JointResult.PERFECT -> 50; JointResult.MINOR_LEAK -> 10; else -> 0 }
        totalScore += pts
        if (result != JointResult.PERFECT) anyLeak = true
        if (result == JointResult.MAJOR_LEAK) anyMajorLeak = true
        game.save.recordJoint(result == JointResult.PERFECT)
        when (result) {
            JointResult.PERFECT -> { game.audio.play(Sfx.PERFECT); game.haptics.success() }
            JointResult.MINOR_LEAK -> { game.audio.play(Sfx.SUCCESS) }
            JointResult.MAJOR_LEAK -> { game.audio.play(Sfx.LEAK); game.haptics.error() }
        }
        if (result == JointResult.PERFECT) {
            particles.confetti(w / 2f, pipeCY, 30, intArrayOf(Palette.BLUE_LIGHT, Palette.WHITE, Palette.RED, Palette.AMBER))
        }
    }

    private fun afterTest() {
        jointsDone++; jointIndex++
        checkAchievementPopups()
        val finished = when (mode) {
            GameMode.CAREER -> jointsDone >= jointsTarget
            GameMode.TIME_ATTACK -> timeLeft <= 0f
            GameMode.ENDLESS -> { if (result == JointResult.MAJOR_LEAK) lives--; lives <= 0 }
            else -> true
        }
        if (finished) {
            if (mode == GameMode.CAREER) finishLevel() else endRun()
        } else {
            resetJointScores(); cfg = newJointConfig(); computeGeom(); enterPhase(Phase.SELECT_PIPE)
        }
    }

    private fun resetJointScores() { sSel = 1f; sCut = 0f; sClean = 0f; sDebur = 0f; sCement = 0f; sFsel = 1f; sAlign = 0f; sHold = 0f }

    private fun finishLevel() {
        val avgQ = if (jointsDone > 0) levelQualitySum / jointsDone else 0f
        endStars = when { avgQ >= 0.8f && !anyMajorLeak -> 3; avgQ >= 0.58f -> 2; else -> 1 }
        val prevRank = game.save.rank
        game.save.recordCareerResult(levelIndex, endStars, leakFree = !anyLeak)
        if (game.save.rank != prevRank) unlockedRank = game.save.rank
        checkAchievementPopups()
        ended = true; endFailed = false; endT = 0f
        game.audio.play(Sfx.STAR)
    }

    private fun endRun() {
        ended = true; endFailed = false; endT = 0f
        if (mode == GameMode.TIME_ATTACK) { newBest = jointsDone > game.save.bestTimeAttack; game.save.recordTimeAttack(jointsDone) }
        if (mode == GameMode.ENDLESS) { newBest = jointsDone > game.save.bestEndless; game.save.recordEndless(jointsDone) }
        checkAchievementPopups()
    }

    private fun onTimeUp() {
        if (mode == GameMode.TIME_ATTACK) endRun()
        else if (mode == GameMode.CAREER) { ended = true; endFailed = true; endT = 0f; game.audio.play(Sfx.ERROR) }
    }

    private fun checkAchievementPopups() {
        val fresh = game.save.pollNewAchievements()
        if (fresh.isNotEmpty()) { fresh.forEach { achQueue.addLast(it) }; game.audio.play(Sfx.STAR) }
    }

    // ---------- Button routing helpers ----------
    private fun tap(b: Button, e: TouchEvent): Boolean {
        when (e.kind) {
            TouchEvent.Kind.DOWN -> if (b.onDown(e.x, e.y)) { armed = b; if (b === pauseBtn) game.haptics.tick(); return true }
            TouchEvent.Kind.MOVE -> if (armed === b && !b.contains(e.x, e.y)) { b.cancel(); armed = null }
            TouchEvent.Kind.UP -> {
                val hit = b.onUp(e.x, e.y)
                if (b === pauseBtn) pauseBtnFired = hit
                if (b === cta) ctaFired = hit
                if (hit) { game.audio.play(Sfx.CLICK); if (armed === b) armed = null; return true }
            }
            else -> {}
        }
        return false
    }

    private fun handlePauseButtons(e: TouchEvent) {
        for (b in listOf(resumeB, restartB, exitB)) {
            if (tap(b, e) && e.kind == TouchEvent.Kind.UP) when (b.id) {
                "resume" -> { paused = false }
                "restart" -> game.setRoot(PlayScreen(game, mode, levelIndex))
                "exit" -> game.setRoot(MenuScreen(game))
            }
        }
    }

    private fun handleEndButtons(e: TouchEvent) {
        if (mode == GameMode.CAREER && !endFailed) {
            if (tap(nextB, e) && e.kind == TouchEvent.Kind.UP) {
                val n = levelIndex + 1
                if (n < Levels.all.size) game.setRoot(PlayScreen(game, GameMode.CAREER, n)) else game.setRoot(MenuScreen(game))
            }
        }
        if (tap(retryB, e) && e.kind == TouchEvent.Kind.UP) game.setRoot(PlayScreen(game, mode, levelIndex))
        if (tap(menuB, e) && e.kind == TouchEvent.Kind.UP) game.setRoot(MenuScreen(game))
    }

    private fun openPause() { paused = true; game.audio.play(Sfx.CLICK) }

    private fun optHit(e: TouchEvent): Int { for (i in optRects.indices) if (optRects[i].contains(e.x, e.y)) return i; return -1 }

    override fun onBack(): Boolean {
        when { ended -> game.setRoot(MenuScreen(game)); paused -> paused = false; else -> openPause() }
        return true
    }

    // ---------- Render ----------
    override fun render(c: Canvas) {
        Decor.workshop(p, c, phaseT)
        p.rect(c, 0f, 0f, w, h, 0x22000000)
        if (flash > 0f) p.rect(c, 0f, 0f, w, h, Painter.withAlpha(Palette.RED, (flash * 90).toInt()))

        renderStage(c)
        particles.render(p, c)
        renderHud(c)
        renderPhaseUi(c)

        if (paused) renderPause(c)
        if (ended) renderEnd(c)
        renderAchToast(c)
    }

    private fun heart(c: Canvas, cx: Float, cy: Float, r: Float) {
        p.circle(c, cx - r * 0.5f, cy - r * 0.15f, r * 0.58f, Palette.RED)
        p.circle(c, cx + r * 0.5f, cy - r * 0.15f, r * 0.58f, Palette.RED)
        val path = p.path { pa -> pa.moveTo(cx - r, cy + r * 0.05f); pa.lineTo(cx + r, cy + r * 0.05f); pa.lineTo(cx, cy + r * 1.15f); pa.close() }
        p.fillPath(c, path, Palette.RED)
    }

    private fun renderAchToast(c: Canvas) {
        val a = achQueue.firstOrNull() ?: return
        val slide = Geom.easeOutCubic((achT * 3f).coerceIn(0f, 1f)) * (1f - Geom.easeInCubic(((achT - 2.4f) / 0.4f).coerceIn(0f, 1f)))
        val cy = h * 0.6f - (1f - slide) * p.dp(40f)
        val r = RectF(w * 0.12f, cy - p.dp(56f), w * 0.88f, cy + p.dp(56f))
        p.panel(c, r, Painter.withAlpha(0xFF1C4E78.toInt(), (slide * 245).toInt()), p.dp(28f))
        p.circle(c, r.left + p.dp(60f), r.centerY(), p.dp(34f), Painter.withAlpha(Palette.AMBER, (slide * 255).toInt()))
        p.textCentered(c, "★", r.left + p.dp(60f), r.centerY(), p.dp(40f), Painter.withAlpha(Palette.WHITE, (slide * 255).toInt()))
        p.text(c, "Achievement: ${a.title}", r.left + p.dp(110f), r.centerY() + p.dp(8f), p.dp(28f), Painter.withAlpha(Palette.WHITE, (slide * 255).toInt()), Paint.Align.LEFT)
    }

    private fun renderStage(c: Canvas) {
        // pipe + fitting depending on phase
        val showFitting = phase >= Phase.ALIGN
        val printed = "${cfg.pipe.label} CPVC"
        val cementAmt = if (phase >= Phase.CEMENT) (if (phase == Phase.CEMENT) cementCover else 1f) else 0f

        if (phase == Phase.JOIN || phase == Phase.TEST) {
            val insert = if (phase == Phase.JOIN) holdProgress else 1f
            val gap = tubeR * 2.2f * (1f - insert)
            PipeRenderer.fitting(p, c, cfg.fitting, fitX + gap, pipeCY, tubeR, fitAngle)
            PipeRenderer.pipe(p, c, pipeLeft, pipeCY, pipeLen + (fitX - (pipeLeft + pipeLen)) - gap - tubeR, tubeR, printed = printed, cementAmt = 1f, openRight = false)
            if (phase == Phase.TEST) {
                PipeRenderer.water(p, c, pipeLeft, pipeCY, pipeLen + (fitX - (pipeLeft + pipeLen)) - tubeR, tubeR, waterFill)
                if (waterFill > 0.7f && result != JointResult.PERFECT) {
                    val sev = if (result == JointResult.MAJOR_LEAK) 6 else 2
                    particles.spray(fitX - tubeR, pipeCY, -1.2f, 1.4f, sev, 0xAA29B6F6.toInt(), 320f, tubeR * 0.18f, 700f, 0.5f)
                }
            }
        } else {
            if (showFitting) PipeRenderer.fitting(p, c, cfg.fitting, fitX, pipeCY, tubeR, fitAngle, highlight = phase == Phase.ALIGN)
            PipeRenderer.pipe(p, c, pipeLeft, pipeCY, pipeLen, tubeR, printed = printed, cementAmt = cementAmt)
        }
    }

    private fun renderHud(c: Canvas) {
        // top bar
        p.rect(c, 0f, 0f, w, h * 0.115f, 0x66000000)
        p.text(c, modeTitle(), p.dp(28f), h * 0.05f, p.dp(34f), Palette.WHITE, Paint.Align.LEFT)
        // timer / lives center
        when (mode) {
            GameMode.CAREER, GameMode.TIME_ATTACK -> {
                val tcol = if (timeLeft < 8f) Palette.RED else Palette.WHITE
                p.text(c, "${timeLeft.toInt()}s", w / 2f, h * 0.052f, p.dp(40f), tcol)
            }
            GameMode.ENDLESS -> {
                val n = lives.coerceAtLeast(0)
                val r = p.dp(14f)
                val total = n * (r * 2.6f)
                var hx = w / 2f - total / 2f + r
                repeat(n) { heart(c, hx, h * 0.045f, r); hx += r * 2.6f }
            }
            else -> {}
        }
        // score / joints right-ish
        val rightX = w - p.dp(120f)
        p.text(c, scoreLabel(), rightX, h * 0.05f, p.dp(30f), Palette.AMBER, Paint.Align.RIGHT)
        // small can branding
        Decor.cementCan(p, c, p.dp(46f), h * 0.205f, p.dp(0.32f))
        pauseBtn.draw(p, c)
        // instruction
        p.text(c, instruction, w / 2f, h * 0.16f, p.dp(36f), Palette.WHITE)
        p.text(c, stepLabel(), w / 2f, h * 0.205f, p.dp(24f), Palette.BLUE_LIGHT, bold = false)
    }

    private fun renderPhaseUi(c: Canvas) {
        when (phase) {
            Phase.SELECT_PIPE -> drawPipeOptions(c)
            Phase.CUT -> drawCut(c)
            Phase.CLEAN -> drawClean(c)
            Phase.DEBUR -> drawDebur(c)
            Phase.CEMENT -> drawCement(c)
            Phase.FIT_SELECT -> drawFittingOptions(c)
            Phase.ALIGN -> drawAlign(c)
            Phase.JOIN -> drawJoin(c)
            Phase.TEST -> drawTest(c)
        }
    }

    private fun drawPipeOptions(c: Canvas) {
        for (i in optRects.indices) {
            val r = optRects[i]; val ps = cfg.pipeOptions[i]
            p.panel(c, r, Palette.PANEL, p.dp(24f))
            p.border(c, RectF(r.left + 2, r.top + 2, r.right - 2, r.bottom - 2), 0x33FFFFFF, p.dp(2f), p.dp(24f))
            // pipe cross-section circle sized to relDia
            val cr = r.height() * 0.18f * (0.7f + ps.relDia * 0.4f)
            p.circle(c, r.centerX(), r.centerY() - r.height() * 0.08f, cr, PipeRenderer.CREAM)
            p.circle(c, r.centerX(), r.centerY() - r.height() * 0.08f, cr * 0.5f, PipeRenderer.darken(PipeRenderer.CREAM, 0.5f))
            p.textCentered(c, ps.label, r.centerX(), r.bottom - r.height() * 0.2f, r.height() * 0.2f, Palette.WHITE)
        }
    }

    private fun drawFittingOptions(c: Canvas) {
        for (i in optRects.indices) {
            val r = optRects[i]; val ft = cfg.fittingOptions[i]
            p.panel(c, r, Palette.PANEL, p.dp(24f))
            p.border(c, RectF(r.left + 2, r.top + 2, r.right - 2, r.bottom - 2), 0x33FFFFFF, p.dp(2f), p.dp(24f))
            PipeRenderer.fitting(p, c, ft, r.centerX(), r.centerY() - r.height() * 0.08f, r.height() * 0.1f, 0f)
            p.textCentered(c, ft.label, r.centerX(), r.bottom - r.height() * 0.18f, r.height() * 0.16f, Palette.WHITE)
        }
    }

    private fun drawCut(c: Canvas) {
        val gx = pipeLeft + pipeLen * 0.5f
        p.line(c, gx, pipeCY - tubeR * 1.5f, gx, pipeCY + tubeR * 1.5f, 0x55FFFFFF, p.dp(3f))
        val band = pipeLen * 0.06f
        p.rect(c, gx - band, pipeCY - tubeR * 1.6f, gx + band, pipeCY + tubeR * 1.6f, 0x3343A047, p.dp(6f))
        val mx = pipeLeft + pipeLen * cutMarker
        // pipe-cutter head
        p.rect(c, mx - p.dp(7f), pipeCY - tubeR * 2.1f, mx + p.dp(7f), pipeCY + tubeR * 2.1f, Palette.RED, p.dp(4f))
        p.circle(c, mx, pipeCY - tubeR * 2.1f, p.dp(16f), Palette.RED)
        p.circle(c, mx, pipeCY - tubeR * 2.1f, p.dp(8f), Palette.WHITE)
        if (!cutDone) p.text(c, "TAP TO CUT", w / 2f, h * 0.78f, p.dp(34f), Palette.WHITE)
        else p.text(c, "${(sCut * 100).toInt()}%", w / 2f, h * 0.78f, p.dp(40f), Palette.GREEN)
    }

    private fun drawClean(c: Canvas) {
        // dirt overlay over uncleaned segments
        val segW = pipeLen / cleanSeg.size
        for (i in cleanSeg.indices) {
            val a = (1f - cleanSeg[i]).coerceIn(0f, 1f)
            if (a > 0.01f) p.rect(c, pipeLeft + i * segW, pipeCY - tubeR * 0.9f, pipeLeft + (i + 1) * segW, pipeCY + tubeR * 0.9f, Painter.withAlpha(0xFF6E5A33.toInt(), (a * 200).toInt()), tubeR * 0.2f)
        }
        // cloth cursor
        if (ptrDown) p.rect(c, ptrX - tubeR * 0.7f, ptrY - tubeR * 0.5f, ptrX + tubeR * 0.7f, ptrY + tubeR * 0.5f, 0xCC42A5F5.toInt(), tubeR * 0.2f)
        bar(c, Loc.t("cleanliness"), cleanCover)
        p.text(c, "SWIPE TO CLEAN", w / 2f, h * 0.78f, p.dp(32f), Palette.WHITE)
    }

    private fun drawDebur(c: Canvas) {
        for (b in burrs) {
            val by = pipeCY + b * tubeR
            val bx = pipeLeft + pipeLen
            val path = p.path { pa -> pa.moveTo(bx, by - tubeR * 0.22f); pa.lineTo(bx + tubeR * 0.5f, by); pa.lineTo(bx, by + tubeR * 0.22f); pa.close() }
            p.fillPath(c, path, Palette.RED)
        }
        p.text(c, "TAP THE BURRS  (${burrs.size})", w / 2f, h * 0.78f, p.dp(32f), Palette.WHITE)
    }

    private fun drawCement(c: Canvas) {
        // big Weldrite can prop
        Decor.cementCan(p, c, w * 0.84f, h * 0.36f, p.dp(0.7f), glow = true)
        // brush cursor
        if (ptrDown) {
            p.circle(c, ptrX, ptrY, tubeR * 0.5f, 0xCC8FCAFF.toInt())
            p.line(c, ptrX, ptrY, ptrX, ptrY - tubeR * 1.6f, 0xFF8D6E63.toInt(), tubeR * 0.3f)
        }
        bar(c, Loc.t("coverage"), cementCover)
        p.text(c, "DRAG OVER THE PIPE END", w / 2f, h * 0.78f, p.dp(30f), Palette.WHITE)
    }

    private fun drawAlign(c: Canvas) {
        val tx = fitX; val ty = pipeCY
        // target arrow: the receiving socket should face left toward the pipe
        val ay = ty + tubeR * 4.0f
        val ap = p.path { pa -> pa.moveTo(tx - p.dp(42f), ay); pa.lineTo(tx - p.dp(14f), ay - p.dp(16f)); pa.lineTo(tx - p.dp(14f), ay + p.dp(16f)); pa.close() }
        p.fillPath(c, ap, Palette.GREEN)
        p.text(c, "ALIGN SOCKET", tx + p.dp(36f), ay + p.dp(9f), p.dp(24f), Palette.GREEN, Paint.Align.LEFT, bold = false)
        val err = abs(fitAngle - fitTarget)
        val good = err < Geom.lerp(14f, 7f, cfg.difficulty)
        p.text(c, "${err.toInt()}°", tx, ty - tubeR * 4.2f, p.dp(34f), if (good) Palette.GREEN else Palette.WHITE)
        cta.label = Loc.t("confirm"); cta.style = if (good) BtnStyle.SUCCESS else BtnStyle.PRIMARY
        cta.draw(p, c)
        p.text(c, "DRAG TO ROTATE", w / 2f, h * 0.78f, p.dp(28f), Palette.OFFWHITE, bold = false)
    }

    private fun drawJoin(c: Canvas) {
        val tx = fitX - tubeR * 0.4f; val ty = pipeCY
        // hold target ring
        p.ring(c, tx, ty, tubeR * 2.6f, p.dp(8f), 0x55FFFFFF)
        p.arc(c, tx, ty, tubeR * 2.6f, -90f, 360f * holdProgress, Palette.GREEN, p.dp(10f))
        p.textCentered(c, Loc.t("hold"), tx, ty - tubeR * 3.4f, p.dp(30f), if (ptrDown) Palette.GREEN else Palette.WHITE)
        if (!ptrDown && holdProgress < 0.05f) p.text(c, "PRESS & HOLD HERE", w / 2f, h * 0.8f, p.dp(30f), Palette.WHITE)
    }

    private fun drawTest(c: Canvas) {
        if (waterFill >= 1f) {
            val (label, col) = when (result) {
                JointResult.PERFECT -> Loc.t("perfect_joint") to Palette.GREEN
                JointResult.MINOR_LEAK -> Loc.t("minor_leak") to Palette.AMBER
                JointResult.MAJOR_LEAK -> Loc.t("major_leak") to Palette.RED
            }
            val r = RectF(w * 0.12f, h * 0.7f, w * 0.88f, h * 0.84f)
            p.panel(c, r, Painter.withAlpha(col, 230), p.dp(28f))
            p.textCentered(c, label, r.centerX(), r.centerY(), p.dp(44f), Palette.WHITE)
            p.text(c, "${Loc.t("tap_to_start")}", w / 2f, h * 0.9f, p.dp(24f), Palette.OFFWHITE, alpha = (Geom.pulse(phaseT, 1.2f) * 180 + 50).toInt(), bold = false)
        } else {
            p.text(c, Loc.t("pressure_test") + "...", w / 2f, h * 0.78f, p.dp(34f), Palette.BLUE_LIGHT)
        }
    }

    private fun bar(c: Canvas, label: String, v: Float) {
        val l = w * 0.18f; val r = w * 0.82f; val y = h * 0.72f
        p.text(c, label, w / 2f, y - p.dp(14f), p.dp(26f), Palette.OFFWHITE, bold = false)
        p.rect(c, l, y, r, y + p.dp(22f), 0x44000000, p.dp(11f))
        p.rect(c, l, y, l + (r - l) * v.coerceIn(0f, 1f), y + p.dp(22f), Palette.GREEN, p.dp(11f))
    }

    // ---------- Overlays ----------
    private fun renderPause(c: Canvas) {
        p.rect(c, 0f, 0f, w, h, 0xCC0A1F36.toInt())
        p.text(c, Loc.t("paused"), w / 2f, h * 0.3f, p.dp(64f), Palette.WHITE)
        resumeB.label = Loc.t("resume"); restartB.label = Loc.t("restart"); exitB.label = Loc.t("exit")
        resumeB.draw(p, c); restartB.draw(p, c); exitB.draw(p, c)
    }

    private fun renderEnd(c: Canvas) {
        p.rect(c, 0f, 0f, w, h, 0xE60A1F36.toInt())
        val cx = w / 2f
        if (mode == GameMode.CAREER && !endFailed) {
            p.text(c, "${Loc.t("level")} ${levelIndex + 1}", cx, h * 0.18f, p.dp(40f), Palette.OFFWHITE, bold = false)
            p.text(c, if (endStars == 3) Loc.t("perfect_joint") else "COMPLETE!", cx, h * 0.25f, p.dp(54f), Palette.WHITE)
            val pop = Geom.easeOutBack((endT * 1.4f).coerceIn(0f, 1f))
            Decor.starRow(p, c, cx, h * 0.36f, p.dp(90f) * (0.6f + pop * 0.4f), endStars, 3, p.dp(120f))
            p.text(c, "${Loc.t("score")}: $totalScore", cx, h * 0.5f, p.dp(40f), Palette.AMBER)
            unlockedRank?.let { p.text(c, "${Loc.t("unlocked_rank")} ${it.title}", cx, h * 0.55f, p.dp(30f), Palette.BLUE_LIGHT) }
            nextB.label = Loc.t("next"); retryB.label = Loc.t("retry"); menuB.label = Loc.t("menu")
            nextB.visible = levelIndex + 1 < Levels.all.size
            nextB.draw(p, c); retryB.draw(p, c); menuB.draw(p, c)
        } else if (mode == GameMode.CAREER && endFailed) {
            p.text(c, Loc.t("time_up"), cx, h * 0.3f, p.dp(60f), Palette.RED)
            p.text(c, "${Loc.t("joints")}: $jointsDone / $jointsTarget", cx, h * 0.4f, p.dp(36f), Palette.WHITE)
            retryB.label = Loc.t("retry"); menuB.label = Loc.t("menu")
            nextB.visible = false
            retryB.draw(p, c); menuB.draw(p, c)
        } else {
            p.text(c, if (mode == GameMode.TIME_ATTACK) Loc.t("time_up") else Loc.t("major_leak"), cx, h * 0.22f, p.dp(54f), if (mode == GameMode.TIME_ATTACK) Palette.AMBER else Palette.RED)
            p.text(c, "${Loc.t("joints")}: $jointsDone", cx, h * 0.34f, p.dp(56f), Palette.WHITE)
            p.text(c, "${Loc.t("score")}: $totalScore", cx, h * 0.42f, p.dp(36f), Palette.AMBER)
            if (newBest) p.text(c, Loc.t("new_best"), cx, h * 0.49f, p.dp(40f), Palette.GREEN)
            retryB.label = Loc.t("retry"); menuB.label = Loc.t("menu")
            nextB.visible = false
            retryB.draw(p, c); menuB.draw(p, c)
        }
    }

    // ---------- Labels ----------
    private fun modeTitle(): String = when (mode) {
        GameMode.CAREER -> "${Loc.t("level")} ${levelIndex + 1}"
        GameMode.TIME_ATTACK -> Loc.t("time_attack")
        GameMode.ENDLESS -> Loc.t("endless")
        GameMode.TUTORIAL -> Loc.t("tutorial")
    }

    private fun scoreLabel(): String = when (mode) {
        GameMode.CAREER -> "${Loc.t("joints")} ${jointsDone + 1}/$jointsTarget"
        else -> "${Loc.t("joints")} $jointsDone"
    }

    private fun stepLabel(): String {
        val n = Phase.entries.indexOf(phase) + 1
        return "Step $n / ${Phase.entries.size}"
    }
}
