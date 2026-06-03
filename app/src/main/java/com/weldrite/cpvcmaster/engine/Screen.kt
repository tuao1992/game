package com.weldrite.cpvcmaster.engine

import android.graphics.Canvas
import com.weldrite.cpvcmaster.audio.Sfx
import com.weldrite.cpvcmaster.ui.Button

/** A normalized pointer/back event delivered to the active screen on the game thread. */
class TouchEvent(val kind: Kind, val x: Float, val y: Float) {
    enum class Kind { DOWN, MOVE, UP, BACK }
}

/**
 * Base class for every full-screen scene. Lifecycle and the update/render/touch
 * methods are all invoked on the single game-loop thread.
 */
abstract class Screen(val game: Game) {
    val w: Float get() = game.width.toFloat()
    val h: Float get() = game.height.toFloat()
    val p: Painter get() = game.painter

    /** Called when the screen becomes active (top of the stack). */
    open fun onShow() {}
    /** Called when the screen leaves the top of the stack. */
    open fun onHide() {}
    /** Called when the surface size changes; recompute layout here. */
    open fun layout() {}

    abstract fun update(dt: Float)
    abstract fun render(c: Canvas)
    open fun onTouch(e: TouchEvent) {}

    /** Return true if the back action was consumed; false lets the stack pop/exit. */
    open fun onBack(): Boolean = false

    // ---- Shared button handling ----
    protected val buttons = ArrayList<Button>()
    private var armed: Button? = null

    /** Feed a touch event to [buttons]; returns the id of a button activated on UP. */
    protected fun buttonsTouch(e: TouchEvent): String? {
        when (e.kind) {
            TouchEvent.Kind.DOWN -> {
                armed = null
                for (b in buttons) if (b.onDown(e.x, e.y)) { armed = b; game.haptics.tick(); break }
            }
            TouchEvent.Kind.MOVE -> armed?.let { if (!it.contains(e.x, e.y)) { it.cancel(); armed = null } }
            TouchEvent.Kind.UP -> {
                var id: String? = null
                for (b in buttons) if (b.onUp(e.x, e.y)) id = b.id else b.cancel()
                armed = null
                if (id != null) game.audio.play(Sfx.CLICK)
                return id
            }
            else -> {}
        }
        return null
    }

    protected fun updateButtons(dt: Float) { for (b in buttons) b.update(dt) }
    protected fun drawButtons(c: Canvas) { for (b in buttons) b.draw(p, c) }
}
