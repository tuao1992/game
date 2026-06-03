package com.weldrite.cpvcmaster.ui

import android.graphics.Canvas
import android.graphics.RectF
import com.weldrite.cpvcmaster.engine.Painter
import kotlin.math.min

object Palette {
    const val BLUE = 0xFF1565C0.toInt()
    const val BLUE_DARK = 0xFF0E2A47.toInt()
    const val BLUE_MID = 0xFF12395F.toInt()
    const val BLUE_LIGHT = 0xFF42A5F5.toInt()
    const val WHITE = 0xFFFFFFFF.toInt()
    const val OFFWHITE = 0xFFEAF2FB.toInt()
    const val RED = 0xFFE53935.toInt()
    const val RED_DARK = 0xFFB71C1C.toInt()
    const val GREEN = 0xFF43A047.toInt()
    const val AMBER = 0xFFFFC107.toInt()
    const val INK = 0xFF0A1F36.toInt()
    const val PANEL = 0xFF173A5E.toInt()
    const val MUTED = 0xFF9FB6CE.toInt()
}

enum class BtnStyle { PRIMARY, SECONDARY, DANGER, GHOST, SUCCESS }

/** Immediate-mode-ish button: screens lay it out, feed it touch, and draw it. */
class Button(val id: String) {
    val rect = RectF()
    var label = ""
    var style = BtnStyle.PRIMARY
    var enabled = true
    var visible = true
    var textScale = 0.4f
    private var down = false
    private var press = 0f

    fun set(l: Float, t: Float, r: Float, b: Float) { rect.set(l, t, r, b) }
    fun contains(x: Float, y: Float) = enabled && visible && rect.contains(x, y)
    fun onDown(x: Float, y: Float): Boolean { if (contains(x, y)) { down = true; return true }; return false }
    fun onUp(x: Float, y: Float): Boolean { val hit = down && contains(x, y); down = false; return hit }
    fun cancel() { down = false }
    fun update(dt: Float) { val target = if (down) 1f else 0f; press += (target - press) * min(1f, dt * 16f) }

    fun draw(p: Painter, c: Canvas) {
        if (!visible) return
        val shrink = press * (rect.height() * 0.05f)
        val r = RectF(rect.left + shrink, rect.top + shrink, rect.right - shrink, rect.bottom - shrink)
        val radius = r.height() * 0.28f
        val (bg, fg, bord) = when (style) {
            BtnStyle.PRIMARY -> Triple(Palette.BLUE, Palette.WHITE, Palette.BLUE_LIGHT)
            BtnStyle.SECONDARY -> Triple(Palette.WHITE, Palette.BLUE_DARK, Palette.BLUE_LIGHT)
            BtnStyle.DANGER -> Triple(Palette.RED, Palette.WHITE, 0xFFFF6F60.toInt())
            BtnStyle.SUCCESS -> Triple(Palette.GREEN, Palette.WHITE, 0xFF80E27E.toInt())
            BtnStyle.GHOST -> Triple(0x33FFFFFF, Palette.WHITE, 0x66FFFFFF)
        }
        val tint = if (!enabled) 0x66000000 else 0
        p.panel(c, r, blend(bg, tint), radius, shadow = style != BtnStyle.GHOST)
        p.border(c, RectF(r.left + 2, r.top + 2, r.right - 2, r.bottom - 2), Painter.withAlpha(bord, if (enabled) 150 else 60), p.dp(3f), radius)
        p.textCentered(c, label, r.centerX(), r.centerY(), r.height() * textScale, if (enabled) fg else Painter.withAlpha(fg, 130))
    }

    private fun blend(c: Int, overlay: Int): Int {
        if (overlay == 0) return c
        val a = (overlay ushr 24) / 255f
        return Painter.mix(c, 0xFF000000.toInt(), a * 0.5f)
    }
}

/** On/off pill toggle. */
class Toggle(val id: String) {
    val rect = RectF()
    var on = false
    private var anim = 0f
    fun set(l: Float, t: Float, r: Float, b: Float) { rect.set(l, t, r, b) }
    fun contains(x: Float, y: Float) = rect.contains(x, y)
    fun update(dt: Float) { val target = if (on) 1f else 0f; anim += (target - anim) * min(1f, dt * 16f) }
    fun draw(p: Painter, c: Canvas) {
        val radius = rect.height() / 2f
        val track = Painter.mix(0xFF5A6B7B.toInt(), Palette.GREEN, anim)
        p.rrect(c, rect, track, radius)
        val pad = rect.height() * 0.12f
        val kr = (rect.height() - pad * 2) / 2f
        val cx = (rect.left + radius) + (rect.width() - radius * 2) * anim
        p.circle(c, cx, rect.centerY(), kr, Palette.WHITE)
    }
}
