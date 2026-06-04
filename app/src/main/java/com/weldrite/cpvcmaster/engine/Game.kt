package com.weldrite.cpvcmaster.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import com.weldrite.cpvcmaster.audio.AudioEngine
import com.weldrite.cpvcmaster.audio.Sfx
import com.weldrite.cpvcmaster.data.Loc
import com.weldrite.cpvcmaster.data.SaveManager
import com.weldrite.cpvcmaster.screens.SplashScreen

/** Implemented by the hosting Activity so the engine stays decoupled from it. */
interface GameHost {
    fun exitToBackground()
}

/**
 * Owns the screen stack, global services (save / audio / haptics / painter) and a
 * simple cross-fade between screens. All methods here run on the game-loop thread.
 */
class Game(val context: Context, val host: GameHost) {

    var width = 1
    var height = 1

    val save = SaveManager(context)
    val audio = AudioEngine()
    val haptics = Haptics(context)
    var painter = Painter(1, 1, save.quality)

    private val stack = ArrayDeque<Screen>()
    private var booted = false

    // Juice: screen shake + hit-stop (freeze-frame)
    private var shakeAmp = 0f
    private var hitStopT = 0f
    private val fxRng = kotlin.random.Random(System.nanoTime())

    /** Request a screen shake; [px] is design-space amplitude (scaled to device + quality). */
    fun shake(px: Float) {
        val q = painter.quality.particleScale.coerceIn(0.5f, 1.3f) // Low quality ≈ reduce-motion
        val a = (px * painter.u * q).coerceAtMost(painter.u * 20f)
        if (a > shakeAmp) shakeAmp = a
    }

    /** Freeze gameplay for [sec] seconds to punch up an impactful moment. */
    fun hitStop(sec: Float) { if (sec > hitStopT) hitStopT = sec }

    // Cross-fade: 0 = idle, 1 = fading out (then run pendingNav), -1 = fading in
    private var transDir = 0
    private var transA = 0f
    private var pendingNav: (() -> Unit)? = null
    private val fadeSpeed = 4.5f

    init {
        Loc.language = save.language
        refreshSettings()
        save.touchDailyStreak()
    }

    val current: Screen? get() = stack.lastOrNull()

    /** Pushes save-backed settings into the live services. */
    fun refreshSettings() {
        audio.musicEnabled = save.musicOn
        audio.sfxEnabled = save.sfxOn
        haptics.enabled = save.hapticsOn
        painter.quality = save.quality
        Loc.language = save.language
    }

    fun resize(w: Int, h: Int) {
        width = w; height = h
        painter.resize(w, h, save.quality)
        if (!booted) {
            booted = true
            setRoot(SplashScreen(this))
        } else {
            stack.forEach { it.layout() }
        }
    }

    fun update(dt: Float) {
        updateTransition(dt)
        if (hitStopT > 0f) hitStopT -= dt else current?.update(dt)
        if (shakeAmp > 0.3f) shakeAmp -= shakeAmp * (dt * 10f).coerceAtMost(1f) else shakeAmp = 0f
    }

    fun render(c: Canvas) {
        val s = current
        if (s != null) {
            if (shakeAmp > 0.3f) {
                c.drawColor(0xFF0E2A47.toInt()) // brand fill so shaken edges never show black
                c.save()
                c.translate((fxRng.nextFloat() - 0.5f) * 2f * shakeAmp, (fxRng.nextFloat() - 0.5f) * 2f * shakeAmp)
                s.render(c)
                c.restore()
            } else s.render(c)
        } else c.drawColor(Color.parseColor("#0E2A47"))
        if (transA > 0f) {
            c.drawColor((((transA * 255).toInt().coerceIn(0, 255)) shl 24))
        }
    }

    fun onTouch(e: TouchEvent) {
        if (transDir != 0) return // ignore input mid-transition
        if (e.kind == TouchEvent.Kind.BACK) { handleBack(); return }
        current?.onTouch(e)
    }

    fun onPause() {
        save.flush()
    }

    // ---- Navigation (each wraps in a fade) ----
    fun setRoot(s: Screen) = navigate {
        stack.forEach { it.onHide() }; stack.clear(); push0(s)
    }
    fun push(s: Screen) = navigate {
        current?.onHide(); push0(s)
    }
    fun replace(s: Screen) = navigate {
        if (stack.isNotEmpty()) stack.removeLast().onHide(); push0(s)
    }
    fun pop() {
        if (stack.size <= 1) return
        navigate {
            stack.removeLast().onHide()
            current?.let { it.layout(); it.onShow() }
        }
    }

    private fun push0(s: Screen) { stack.addLast(s); s.layout(); s.onShow() }

    private fun navigate(action: () -> Unit) {
        if (pendingNav != null) return // a transition is already in flight
        if (!booted2 && stack.isEmpty()) { // very first root: no fade-out needed
            action(); transDir = -1; transA = 1f; booted2 = true; return
        }
        pendingNav = action
        transDir = 1
    }

    private var booted2 = false

    private fun updateTransition(dt: Float) {
        when (transDir) {
            1 -> { transA += dt * fadeSpeed; if (transA >= 1f) { transA = 1f; pendingNav?.invoke(); pendingNav = null; transDir = -1 } }
            -1 -> { transA -= dt * fadeSpeed; if (transA <= 0f) { transA = 0f; transDir = 0 } }
        }
    }

    fun handleBack() {
        val s = current
        if (s != null && s.onBack()) return
        if (stack.size > 1) { audio.play(Sfx.CLICK); pop() } else host.exitToBackground()
    }
}
