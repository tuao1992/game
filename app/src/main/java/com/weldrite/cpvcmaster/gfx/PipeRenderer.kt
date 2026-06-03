package com.weldrite.cpvcmaster.gfx

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import com.weldrite.cpvcmaster.data.FittingType
import com.weldrite.cpvcmaster.engine.Painter
import com.weldrite.cpvcmaster.ui.Palette

/**
 * Vector renderer for CPVC pipes and fittings. Cylinders are shaded with a
 * cross-axis gradient to read as 3D tubes; openings get a recessed "bore".
 * Cement is drawn as a glossy bluish coat; water as a translucent fill.
 */
object PipeRenderer {

    val CREAM = 0xFFEAE0C8.toInt()
    private val CREAM_EDGE = 0xFFCBBE9C.toInt()
    private val CEMENT = 0xCC8Fcaff.toInt()

    fun lighten(c: Int, f: Float) = Painter.mix(c, 0xFFFFFFFF.toInt(), f)
    fun darken(c: Int, f: Float) = Painter.mix(c, 0xFF000000.toInt(), f)

    /** Horizontal cylinder with top-lit shading. */
    fun cylinderH(p: Painter, c: Canvas, l: Float, t: Float, r: Float, b: Float, base: Int, corner: Float) {
        p.fill.shader = LinearGradient(
            0f, t, 0f, b,
            intArrayOf(darken(base, 0.42f), base, lighten(base, 0.42f), base, darken(base, 0.5f)),
            floatArrayOf(0f, 0.2f, 0.42f, 0.72f, 1f), Shader.TileMode.CLAMP
        )
        p.fill.style = Paint.Style.FILL
        c.drawRoundRect(l, t, r, b, corner, corner, p.fill)
        p.fill.shader = null
    }

    /** Vertical cylinder with side-lit shading. */
    fun cylinderV(p: Painter, c: Canvas, l: Float, t: Float, r: Float, b: Float, base: Int, corner: Float) {
        p.fill.shader = LinearGradient(
            l, 0f, r, 0f,
            intArrayOf(darken(base, 0.42f), base, lighten(base, 0.42f), base, darken(base, 0.5f)),
            floatArrayOf(0f, 0.2f, 0.42f, 0.72f, 1f), Shader.TileMode.CLAMP
        )
        p.fill.style = Paint.Style.FILL
        c.drawRoundRect(l, t, r, b, corner, corner, p.fill)
        p.fill.shader = null
    }

    /** Recessed bore at a horizontal opening (vertical ellipse). */
    fun boreH(p: Painter, c: Canvas, ex: Float, cy: Float, tubeR: Float, base: Int) {
        val rx = tubeR * 0.34f
        oval(p, c, ex - rx, cy - tubeR, ex + rx, cy + tubeR, darken(base, 0.25f))
        oval(p, c, ex - rx * 0.62f, cy - tubeR * 0.7f, ex + rx * 0.62f, cy + tubeR * 0.7f, darken(base, 0.6f))
    }

    private fun oval(p: Painter, c: Canvas, l: Float, t: Float, r: Float, b: Float, color: Int) {
        p.fill.color = color; p.fill.style = Paint.Style.FILL
        c.drawOval(RectF(l, t, r, b), p.fill)
    }

    /**
     * Draws a horizontal pipe segment. [cementAmt] 0..1 paints a cement coat on
     * the right end; [printed] adds a faint printed spec band.
     */
    fun pipe(p: Painter, c: Canvas, leftX: Float, cy: Float, length: Float, tubeR: Float, base: Int = CREAM, printed: String? = null, cementAmt: Float = 0f, openRight: Boolean = true) {
        val top = cy - tubeR; val bot = cy + tubeR
        cylinderH(p, c, leftX, top, leftX + length, bot, base, tubeR * 0.5f)
        // printed spec band
        if (printed != null) {
            p.text(c, printed, leftX + length * 0.5f, cy + tubeR * 0.18f, tubeR * 0.42f, 0x66000000, Paint.Align.CENTER, bold = true)
        }
        // cement coat near right end
        if (cementAmt > 0f) {
            val coatLen = length * 0.34f * cementAmt.coerceIn(0f, 1f).let { 0.6f + it * 0.4f }
            val cl = leftX + length - coatLen
            p.fill.shader = LinearGradient(0f, top, 0f, bot, intArrayOf(0xFFBFE3FF.toInt(), CEMENT, 0xFF4FA3E0.toInt()), null, Shader.TileMode.CLAMP)
            p.fill.style = Paint.Style.FILL
            c.drawRoundRect(cl, top + tubeR * 0.06f, leftX + length, bot - tubeR * 0.06f, tubeR * 0.4f, tubeR * 0.4f, p.fill)
            p.fill.shader = null
            // wet highlight
            p.rect(c, cl + coatLen * 0.2f, top + tubeR * 0.18f, leftX + length - coatLen * 0.1f, top + tubeR * 0.42f, 0x77FFFFFF, tubeR * 0.2f)
        }
        if (openRight) boreH(p, c, leftX + length, cy, tubeR, base)
        // subtle outline
        p.border(c, RectF(leftX, top, leftX + length, bot), Painter.withAlpha(CREAM_EDGE, 120), p.dp(2f), tubeR * 0.5f)
    }

    /**
     * Draws a fitting centred at (cx,cy), rotated [rotDeg] degrees. [socketR] is
     * the pipe tube radius it receives. [highlight] tints the active socket.
     */
    fun fitting(p: Painter, c: Canvas, type: FittingType, cx: Float, cy: Float, socketR: Float, rotDeg: Float, base: Int = CREAM, highlight: Boolean = false) {
        c.save()
        c.rotate(rotDeg, cx, cy)
        val bodyR = socketR * 1.28f
        val arm = socketR * 3.6f
        when (type) {
            FittingType.COUPLER -> {
                cylinderH(p, c, cx - arm * 0.7f, cy - bodyR, cx + arm * 0.7f, cy + bodyR, base, bodyR * 0.5f)
                socketLip(p, c, cx - arm * 0.7f, cy, bodyR, base)
                socketLip(p, c, cx + arm * 0.7f, cy, bodyR, base)
                boreH(p, c, cx - arm * 0.7f, cy, bodyR * 0.78f, base)
            }
            FittingType.REDUCER -> {
                val bigR = bodyR; val smallR = bodyR * 0.62f
                val path = Path().apply {
                    moveTo(cx - arm * 0.7f, cy - bigR); lineTo(cx - arm * 0.1f, cy - bigR)
                    lineTo(cx + arm * 0.7f, cy - smallR); lineTo(cx + arm * 0.7f, cy + smallR)
                    lineTo(cx - arm * 0.1f, cy + bigR); lineTo(cx - arm * 0.7f, cy + bigR); close()
                }
                p.fill.shader = LinearGradient(0f, cy - bigR, 0f, cy + bigR, intArrayOf(darken(base, 0.4f), lighten(base, 0.4f), darken(base, 0.5f)), null, Shader.TileMode.CLAMP)
                p.fill.style = Paint.Style.FILL; c.drawPath(path, p.fill); p.fill.shader = null
                socketLip(p, c, cx - arm * 0.7f, cy, bigR, base)
                boreH(p, c, cx - arm * 0.7f, cy, bigR * 0.78f, base)
            }
            FittingType.ELBOW -> {
                // horizontal arm (left) + vertical arm (up), filled corner
                cylinderH(p, c, cx - arm, cy - bodyR, cx + bodyR, cy + bodyR, base, bodyR * 0.5f)
                cylinderV(p, c, cx - bodyR, cy - arm, cx + bodyR, cy + bodyR, base, bodyR * 0.5f)
                socketLip(p, c, cx - arm, cy, bodyR, base)
                socketLipV(p, c, cx, cy - arm, bodyR, base)
                boreH(p, c, cx - arm, cy, bodyR * 0.78f, base)
            }
            FittingType.TEE -> {
                cylinderH(p, c, cx - arm, cy - bodyR, cx + arm, cy + bodyR, base, bodyR * 0.5f)
                cylinderV(p, c, cx - bodyR, cy - arm, cx + bodyR, cy + bodyR, base, bodyR * 0.5f)
                socketLip(p, c, cx - arm, cy, bodyR, base)
                socketLip(p, c, cx + arm, cy, bodyR, base)
                socketLipV(p, c, cx, cy - arm, bodyR, base)
                boreH(p, c, cx - arm, cy, bodyR * 0.78f, base)
            }
        }
        if (highlight) {
            p.ring(c, cx - arm, cy, bodyR + p.dp(8f), p.dp(5f), Palette.GREEN)
        }
        c.restore()
    }

    private fun socketLip(p: Painter, c: Canvas, ex: Float, cy: Float, bodyR: Float, base: Int) {
        val w = bodyR * 0.26f
        cylinderH(p, c, ex - w / 2, cy - bodyR * 1.08f, ex + w / 2, cy + bodyR * 1.08f, lighten(base, 0.06f), bodyR * 0.3f)
    }

    private fun socketLipV(p: Painter, c: Canvas, cx: Float, ey: Float, bodyR: Float, base: Int) {
        val hgt = bodyR * 0.26f
        cylinderV(p, c, cx - bodyR * 1.08f, ey - hgt / 2, cx + bodyR * 1.08f, ey + hgt / 2, lighten(base, 0.06f), bodyR * 0.3f)
    }

    /** Translucent water fill inside a horizontal pipe, [fill] 0..1 along length. */
    fun water(p: Painter, c: Canvas, leftX: Float, cy: Float, length: Float, tubeR: Float, fill: Float, color: Int = 0xAA29B6F6.toInt()) {
        val fl = (length * fill.coerceIn(0f, 1f))
        if (fl <= 0f) return
        p.fill.shader = LinearGradient(0f, cy - tubeR, 0f, cy + tubeR, intArrayOf(lighten(color, 0.3f), color, darken(color, 0.2f)), null, Shader.TileMode.CLAMP)
        p.fill.style = Paint.Style.FILL
        c.drawRoundRect(leftX, cy - tubeR * 0.78f, leftX + fl, cy + tubeR * 0.78f, tubeR * 0.4f, tubeR * 0.4f, p.fill)
        p.fill.shader = null
    }
}
