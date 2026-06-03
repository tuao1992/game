package com.weldrite.cpvcmaster.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import com.weldrite.cpvcmaster.data.Quality

/**
 * Central drawing toolkit shared by all screens. Owns reusable Paint/Typeface
 * objects (single render thread, so no synchronization needed) and exposes a
 * compact, consistent vocabulary for panels, buttons, text and shapes.
 *
 * Layout is resolution-independent: [u] is a "design unit" where the screen
 * width maps to 1080 units, so [dp] gives crisp sizing on any device.
 */
class Painter(var w: Int, var h: Int, var quality: Quality) {

    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
    private val tp = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    private val regular = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    private val tmp = RectF()

    val u: Float get() = w / 1080f
    fun dp(v: Float): Float = v * u

    fun resize(nw: Int, nh: Int, q: Quality) { w = nw; h = nh; quality = q }

    // ---- Background ----
    fun clear(c: Canvas, color: Int) = c.drawColor(color)

    /** Vertical two-tone backdrop used across the game. */
    fun backdrop(c: Canvas, top: Int, bottom: Int) {
        val mid = h / 2f
        fill.shader = android.graphics.LinearGradient(0f, 0f, 0f, h.toFloat(), top, bottom, android.graphics.Shader.TileMode.CLAMP)
        fill.style = Paint.Style.FILL
        c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), fill)
        fill.shader = null
    }

    // ---- Shapes ----
    fun rect(c: Canvas, l: Float, t: Float, r: Float, b: Float, color: Int, radius: Float = 0f) {
        fill.color = color; fill.style = Paint.Style.FILL
        if (radius > 0f) c.drawRoundRect(l, t, r, b, radius, radius, fill) else c.drawRect(l, t, r, b, fill)
    }

    fun rrect(c: Canvas, rf: RectF, color: Int, radius: Float) {
        fill.color = color; fill.style = Paint.Style.FILL
        c.drawRoundRect(rf, radius, radius, fill)
    }

    fun border(c: Canvas, rf: RectF, color: Int, width: Float, radius: Float) {
        stroke.color = color; stroke.strokeWidth = width; stroke.style = Paint.Style.STROKE
        c.drawRoundRect(rf, radius, radius, stroke)
    }

    fun circle(c: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        fill.color = color; fill.style = Paint.Style.FILL
        c.drawCircle(cx, cy, r, fill)
    }

    fun ring(c: Canvas, cx: Float, cy: Float, r: Float, width: Float, color: Int) {
        stroke.color = color; stroke.strokeWidth = width; stroke.style = Paint.Style.STROKE
        c.drawCircle(cx, cy, r, stroke)
    }

    fun line(c: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, width: Float) {
        stroke.color = color; stroke.strokeWidth = width; stroke.style = Paint.Style.STROKE
        c.drawLine(x1, y1, x2, y2, stroke)
    }

    /** Soft drop-shadowed rounded panel (shadow faked with a translucent offset copy). */
    fun panel(c: Canvas, rf: RectF, color: Int, radius: Float, shadow: Boolean = true) {
        if (shadow) {
            tmp.set(rf); tmp.offset(0f, dp(6f))
            fill.color = 0x40000000; fill.style = Paint.Style.FILL
            c.drawRoundRect(tmp, radius, radius, fill)
        }
        rrect(c, rf, color, radius)
    }

    // ---- Arcs / progress ----
    fun arc(c: Canvas, cx: Float, cy: Float, r: Float, startDeg: Float, sweepDeg: Float, color: Int, width: Float) {
        stroke.color = color; stroke.strokeWidth = width; stroke.style = Paint.Style.STROKE
        tmp.set(cx - r, cy - r, cx + r, cy + r)
        c.drawArc(tmp, startDeg, sweepDeg, false, stroke)
    }

    // ---- Text ----
    fun text(
        c: Canvas, s: String, x: Float, y: Float, size: Float, color: Int,
        align: Paint.Align = Paint.Align.CENTER, bold: Boolean = true, alpha: Int = 255,
    ) {
        tp.textSize = size; tp.color = color; tp.alpha = alpha
        tp.textAlign = align; tp.typeface = if (bold) this.bold else regular
        c.drawText(s, x, y, tp)
    }

    /** Draws text centered on (cx, cy) both horizontally and vertically. */
    fun textCentered(c: Canvas, s: String, cx: Float, cy: Float, size: Float, color: Int, bold: Boolean = true, alpha: Int = 255) {
        tp.textSize = size; tp.typeface = if (bold) this.bold else regular
        val baseline = cy - (tp.descent() + tp.ascent()) / 2f
        text(c, s, cx, baseline, size, color, Paint.Align.CENTER, bold, alpha)
    }

    fun textWidth(s: String, size: Float, bold: Boolean = true): Float {
        tp.textSize = size; tp.typeface = if (bold) this.bold else regular
        return tp.measureText(s)
    }

    fun lineHeight(size: Float): Float { tp.textSize = size; return tp.descent() - tp.ascent() }

    // ---- Path helper ----
    inline fun path(block: (Path) -> Unit): Path = Path().also(block)

    fun fillPath(c: Canvas, p: Path, color: Int) { fill.color = color; fill.style = Paint.Style.FILL; c.drawPath(p, fill) }
    fun strokePath(c: Canvas, p: Path, color: Int, width: Float) {
        stroke.color = color; stroke.strokeWidth = width; stroke.style = Paint.Style.STROKE; c.drawPath(p, stroke)
    }

    companion object {
        fun withAlpha(color: Int, a: Int): Int = (color and 0x00FFFFFF) or (a shl 24)
        fun mix(c1: Int, c2: Int, t: Float): Int {
            val r = (Color.red(c1) + (Color.red(c2) - Color.red(c1)) * t).toInt()
            val g = (Color.green(c1) + (Color.green(c2) - Color.green(c1)) * t).toInt()
            val b = (Color.blue(c1) + (Color.blue(c2) - Color.blue(c1)) * t).toInt()
            return Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
        }
    }
}
