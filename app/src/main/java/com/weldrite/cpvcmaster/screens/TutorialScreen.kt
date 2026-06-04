package com.weldrite.cpvcmaster.screens

import android.graphics.Canvas
import android.graphics.RectF
import com.weldrite.cpvcmaster.audio.Sfx
import com.weldrite.cpvcmaster.data.FittingType
import com.weldrite.cpvcmaster.data.Loc
import com.weldrite.cpvcmaster.data.PipeSize
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.Geom
import com.weldrite.cpvcmaster.engine.Particles
import com.weldrite.cpvcmaster.engine.Screen
import com.weldrite.cpvcmaster.engine.TouchEvent
import com.weldrite.cpvcmaster.gfx.Decor
import com.weldrite.cpvcmaster.gfx.PipeRenderer
import com.weldrite.cpvcmaster.ui.BtnStyle
import com.weldrite.cpvcmaster.ui.Button
import com.weldrite.cpvcmaster.ui.Palette
import kotlin.math.abs

/** Interactive, skippable tutorial covering the five core skills. */
class TutorialScreen(
    game: Game,
    private val onDone: () -> Unit = { game.setRoot(MenuScreen(game)) },
) : Screen(game) {

    private val particles = Particles().apply { densityScale = game.save.quality.particleScale }
    private val titles = listOf("1. Pipe Preparation", "2. Cement Application", "3. Fitting Alignment", "4. Pipe Insertion", "5. Pressure Testing")
    private val hints = listOf(
        "Tap to cut the pipe square and clean.",
        "Drag across the pipe end to coat it evenly.",
        "Drag to rotate the fitting, then Confirm.",
        "Press & hold until the bond sets.",
        "Run water through to test the joint.",
    )

    private var stage = 0
    private var t = 0f
    private var stageDone = false

    // geometry
    private var pipeLeft = 0f; private var pipeCY = 0f; private var pipeLen = 0f; private var tubeR = 0f; private var fitX = 0f

    // interaction state
    private var marker = 0.1f; private var dir = 1f
    private val cementBuckets = BooleanArray(12); private var cementCover = 0f
    private var fitAngle = 60f
    private var holdProgress = 0f
    private var waterFill = 0f; private var testRun = false

    private var ptrDown = false; private var ptrX = 0f; private var ptrY = 0f; private var lastX = 0f

    private val skip = Button("skip").apply { style = BtnStyle.GHOST; textScale = 0.36f }
    private val action = Button("action").apply { style = BtnStyle.PRIMARY }
    private var armed: Button? = null

    override fun layout() {
        skip.set(w - p.dp(180f), p.dp(34f), w - p.dp(24f), p.dp(34f) + p.dp(72f))
        action.set(w / 2f - p.dp(180f), h * 0.85f, w / 2f + p.dp(180f), h * 0.85f + p.dp(100f))
        tubeR = w * 0.07f
        pipeCY = h * 0.5f
        pipeLeft = w * 0.12f
        pipeLen = w * 0.46f
        fitX = w * 0.76f
        skip.label = Loc.t("skip")
    }

    override fun update(dt: Float) {
        t += dt
        skip.update(dt); action.update(dt); particles.update(dt)
        when (stage) {
            0 -> if (!stageDone) { marker += dir * 0.8f * dt; if (marker > 0.92f) { marker = 0.92f; dir = -1f }; if (marker < 0.08f) { marker = 0.08f; dir = 1f } }
            3 -> {
                val tx = fitX - tubeR * 0.4f
                if (ptrDown && Geom.dist(ptrX, ptrY, tx, pipeCY) < tubeR * 3f) {
                    holdProgress = (holdProgress + dt / 1.6f).coerceAtMost(1f)
                    if (holdProgress >= 1f && !stageDone) { stageDone = true; game.audio.play(Sfx.JOIN_SET); game.haptics.heavy(); game.shake(9f); game.hitStop(0.08f); advanceSoon() }
                } else holdProgress = (holdProgress - dt * 0.5f).coerceAtLeast(0f)
            }
            4 -> if (testRun) { waterFill = (waterFill + dt * 0.7f).coerceAtMost(1f); if (waterFill >= 1f && !stageDone) { stageDone = true; game.audio.play(Sfx.PERFECT); game.shake(10f); game.hitStop(0.08f); particles.confetti(w / 2f, pipeCY, 30, intArrayOf(Palette.BLUE_LIGHT, Palette.WHITE, Palette.RED, Palette.AMBER)) } }
        }
        // action button visibility
        action.visible = when (stage) {
            0, 1 -> stageDone
            2 -> true
            3 -> false
            4 -> true
            else -> false
        }
        action.label = when {
            stage == 2 -> Loc.t("confirm")
            stage == 4 && !testRun -> "Run Test"
            stage == 4 && stageDone -> Loc.t("continue")
            else -> Loc.t("next")
        }
        action.style = if (stage == 4 && !testRun) BtnStyle.SUCCESS else BtnStyle.PRIMARY
    }

    private var advanceTimer = -1f
    private fun advanceSoon() { advanceTimer = 0.7f }

    override fun onTouch(e: TouchEvent) {
        when (e.kind) {
            TouchEvent.Kind.DOWN -> { ptrDown = true; ptrX = e.x; ptrY = e.y; lastX = e.x }
            TouchEvent.Kind.MOVE -> { ptrX = e.x; ptrY = e.y }
            TouchEvent.Kind.UP -> ptrDown = false
            else -> {}
        }
        if (tapBtn(skip, e)) { game.audio.play(Sfx.CLICK); finish(); return }
        if (action.visible && tapBtn(action, e)) { game.audio.play(Sfx.CLICK); onAction(); if (e.kind == TouchEvent.Kind.MOVE) lastX = e.x; return }

        when (stage) {
            0 -> if (e.kind == TouchEvent.Kind.DOWN && !stageDone) {
                stageDone = true; game.audio.play(Sfx.CUT); game.haptics.medium()
                game.shake(6f); game.hitStop(0.05f)
                particles.spray(pipeLeft + pipeLen * marker, pipeCY, 1.5708f, 1.6f, 14, 0xFFD9C9A0.toInt(), 360f, tubeR * 0.22f, 700f, 0.6f)
            }
            1 -> if (e.kind != TouchEvent.Kind.UP) paintCement(e)
            2 -> if (e.kind == TouchEvent.Kind.MOVE) fitAngle = (fitAngle + (e.x - lastX) * 0.35f).coerceIn(-130f, 130f)
        }
        if (e.kind == TouchEvent.Kind.MOVE) lastX = e.x
    }

    private fun onAction() {
        when (stage) {
            0, 1 -> nextStage()
            2 -> { if (abs(fitAngle) < 12f) { fitAngle = 0f; game.audio.play(Sfx.ALIGN_LOCK) }; nextStage() }
            4 -> if (!testRun) testRun = true else finish()
        }
    }

    private fun paintCement(e: TouchEvent) {
        val coatL = pipeLeft + pipeLen * 0.6f; val coatR = pipeLeft + pipeLen
        if (e.y < pipeCY - tubeR * 2f || e.y > pipeCY + tubeR * 2f) return
        if (e.x < coatL - tubeR || e.x > coatR + tubeR) return
        val rel = ((e.x - coatL) / (coatR - coatL)).coerceIn(0f, 1f)
        val idx = (rel * (cementBuckets.size - 1)).toInt().coerceIn(0, cementBuckets.lastIndex)
        cementBuckets[idx] = true
        if (Math.random() < 0.25) { game.audio.play(Sfx.CEMENT); game.haptics.tick() }
        particles.burst(e.x, e.y, 1, 0xCC8FCAFF.toInt(), 80f, tubeR * 0.12f, 200f, 0.35f)
        cementCover = cementBuckets.count { it }.toFloat() / cementBuckets.size
        if (cementCover >= 0.95f && !stageDone) { stageDone = true; game.haptics.light() }
    }

    private fun nextStage() {
        stage++; stageDone = false; t = 0f
        marker = 0.1f; dir = 1f; holdProgress = 0f; waterFill = 0f; testRun = false
        if (stage >= 5) finish()
    }

    private fun finish() { game.save.markTutorialDone(); onDone() }

    override fun onBack(): Boolean { finish(); return true }

    override fun render(c: Canvas) {
        Decor.workshop(p, c, t)
        p.rect(c, 0f, 0f, w, h, 0x33000000)
        if (advanceTimer >= 0f) { advanceTimer -= 1f / 60f; if (advanceTimer <= 0f) { advanceTimer = -1f; nextStage() } }

        // header
        p.rect(c, 0f, 0f, w, h * 0.12f, 0x66000000)
        p.text(c, Loc.t("tutorial"), p.dp(28f), h * 0.052f, p.dp(34f), Palette.WHITE, android.graphics.Paint.Align.LEFT)
        skip.draw(p, c)
        // progress dots
        val n = 5; val gap = p.dp(36f); var dx = w / 2f - (n - 1) * gap / 2f
        for (i in 0 until n) { p.circle(c, dx, h * 0.16f, p.dp(9f), if (i <= stage) Palette.AMBER else 0x55FFFFFF); dx += gap }
        p.text(c, titles.getOrElse(stage) { "" }, w / 2f, h * 0.23f, p.dp(38f), Palette.WHITE)
        p.text(c, hints.getOrElse(stage) { "" }, w / 2f, h * 0.275f, p.dp(26f), Palette.BLUE_LIGHT, bold = false)

        renderStage(c)
        particles.render(p, c)
        if (action.visible) action.draw(p, c)
    }

    private fun renderStage(c: Canvas) {
        when (stage) {
            0 -> {
                PipeRenderer.pipe(p, c, pipeLeft, pipeCY, pipeLen, tubeR, printed = "3/4\" CPVC")
                val mx = pipeLeft + pipeLen * marker
                p.rect(c, mx - p.dp(7f), pipeCY - tubeR * 2f, mx + p.dp(7f), pipeCY + tubeR * 2f, Palette.RED, p.dp(4f))
                p.circle(c, mx, pipeCY - tubeR * 2f, p.dp(15f), Palette.RED)
                p.text(c, if (stageDone) "Great!" else "TAP TO CUT", w / 2f, h * 0.72f, p.dp(34f), if (stageDone) Palette.GREEN else Palette.WHITE)
            }
            1 -> {
                Decor.cementCan(p, c, w * 0.85f, h * 0.38f, p.dp(0.6f), glow = true, bmp = game.images.cementCan())
                PipeRenderer.pipe(p, c, pipeLeft, pipeCY, pipeLen, tubeR, printed = "3/4\" CPVC", cementAmt = cementCover)
                if (ptrDown) p.circle(c, ptrX, ptrY, tubeR * 0.45f, 0xCC8FCAFF.toInt())
                bar(c, Loc.t("coverage"), cementCover)
            }
            2 -> {
                PipeRenderer.fitting(p, c, FittingType.ELBOW, fitX, pipeCY, tubeR, fitAngle, highlight = abs(fitAngle) < 12f)
                PipeRenderer.pipe(p, c, pipeLeft, pipeCY, pipeLen, tubeR, printed = "3/4\" CPVC", cementAmt = 1f)
                p.text(c, "${abs(fitAngle).toInt()}°", fitX, pipeCY - tubeR * 4f, p.dp(32f), if (abs(fitAngle) < 12f) Palette.GREEN else Palette.WHITE)
            }
            3 -> {
                val gap = tubeR * 2.2f * (1f - holdProgress)
                PipeRenderer.fitting(p, c, FittingType.ELBOW, fitX + gap, pipeCY, tubeR, 0f)
                PipeRenderer.pipe(p, c, pipeLeft, pipeCY, (fitX - pipeLeft) - gap - tubeR, tubeR, printed = "3/4\" CPVC", cementAmt = 1f, openRight = false)
                val tx = fitX - tubeR * 0.4f
                p.ring(c, tx, pipeCY, tubeR * 2.6f, p.dp(8f), 0x55FFFFFF)
                p.arc(c, tx, pipeCY, tubeR * 2.6f, -90f, 360f * holdProgress, Palette.GREEN, p.dp(10f))
                p.text(c, if (ptrDown) Loc.t("hold") else "PRESS & HOLD HERE", w / 2f, h * 0.72f, p.dp(30f), if (ptrDown) Palette.GREEN else Palette.WHITE)
            }
            4 -> {
                PipeRenderer.fitting(p, c, FittingType.ELBOW, fitX, pipeCY, tubeR, 0f)
                PipeRenderer.pipe(p, c, pipeLeft, pipeCY, (fitX - pipeLeft) - tubeR, tubeR, printed = "3/4\" CPVC", cementAmt = 1f, openRight = false)
                PipeRenderer.water(p, c, pipeLeft, pipeCY, (fitX - pipeLeft) - tubeR, tubeR, waterFill)
                if (stageDone) {
                    val r = RectF(w * 0.14f, h * 0.68f, w * 0.86f, h * 0.78f)
                    p.panel(c, r, Palette.GREEN, p.dp(26f))
                    p.textCentered(c, Loc.t("perfect_joint"), r.centerX(), r.centerY(), p.dp(40f), Palette.WHITE)
                }
            }
        }
    }

    private fun bar(c: Canvas, label: String, v: Float) {
        val l = w * 0.18f; val r = w * 0.82f; val y = h * 0.68f
        p.text(c, label, w / 2f, y - p.dp(14f), p.dp(26f), Palette.OFFWHITE, bold = false)
        p.rect(c, l, y, r, y + p.dp(22f), 0x44000000, p.dp(11f))
        p.rect(c, l, y, l + (r - l) * v.coerceIn(0f, 1f), y + p.dp(22f), Palette.GREEN, p.dp(11f))
    }

    private fun tapBtn(b: Button, e: TouchEvent): Boolean {
        when (e.kind) {
            TouchEvent.Kind.DOWN -> if (b.onDown(e.x, e.y)) { armed = b }
            TouchEvent.Kind.MOVE -> if (armed === b && !b.contains(e.x, e.y)) { b.cancel(); armed = null }
            TouchEvent.Kind.UP -> { val hit = b.onUp(e.x, e.y); if (armed === b) armed = null; return hit }
            else -> {}
        }
        return false
    }
}
