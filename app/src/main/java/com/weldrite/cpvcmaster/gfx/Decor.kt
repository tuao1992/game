package com.weldrite.cpvcmaster.gfx

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.weldrite.cpvcmaster.data.Rank
import com.weldrite.cpvcmaster.engine.Geom
import com.weldrite.cpvcmaster.engine.Painter
import com.weldrite.cpvcmaster.ui.Palette
import kotlin.math.cos
import kotlin.math.sin

/** Branding props, stars, rank badges and the shared workshop backdrop. */
object Decor {

    // ---- Workshop backdrop: pegboard wall + tools + workbench ----
    fun workshop(p: Painter, c: Canvas, time: Float, accent: Int = 0xFF13486F.toInt()) {
        val top = Painter.mix(0xFF13486F.toInt(), accent, 0.4f)
        p.backdrop(c, top, Palette.BLUE_DARK)
        val w = p.w.toFloat(); val h = p.h.toFloat()

        // Pegboard holes (subtle), density by quality
        val step = when (p.quality.particleScale) { in 0f..0.5f -> p.dp(110f); in 0.5f..1.2f -> p.dp(76f); else -> p.dp(56f) }
        val holeColor = 0x18000000
        var y = p.dp(60f)
        while (y < h * 0.62f) {
            var x = p.dp(40f)
            while (x < w - p.dp(20f)) {
                p.circle(c, x, y, p.dp(4f), holeColor)
                x += step
            }
            y += step
        }

        // Hanging tools, top corners
        hangingWrench(p, c, w - p.dp(90f), p.dp(40f), p.dp(150f), time)
        hangingCutter(p, c, p.dp(86f), p.dp(40f), p.dp(140f), time + 1.3f)

        // Workbench
        val benchTop = h * 0.78f
        p.rect(c, 0f, benchTop, w, h, 0xFF6D4C2E.toInt())
        p.rect(c, 0f, benchTop, w, benchTop + p.dp(14f), 0xFF8A6239.toInt())
        p.rect(c, 0f, benchTop, w, benchTop + p.dp(5f), 0x55FFFFFF)
    }

    private fun hangingWrench(p: Painter, c: Canvas, x: Float, top: Float, len: Float, time: Float) {
        val sway = Geom.cosWave(time, 0.25f) * p.dp(3f)
        p.line(c, x, top, x + sway, top + len * 0.18f, 0x33000000, p.dp(4f))
        val hx = x + sway; val hy = top + len * 0.18f
        p.line(c, hx, hy, hx, hy + len, 0xFF9FB0C0.toInt(), p.dp(16f))
        p.ring(c, hx, hy + len, p.dp(20f), p.dp(12f), 0xFF9FB0C0.toInt())
        p.ring(c, hx, hy, p.dp(15f), p.dp(10f), 0xFFB7C6D6.toInt())
    }

    private fun hangingCutter(p: Painter, c: Canvas, x: Float, top: Float, len: Float, time: Float) {
        val sway = Geom.cosWave(time, 0.22f) * p.dp(3f)
        val hx = x + sway
        p.line(c, x, top, hx, top + p.dp(20f), 0x33000000, p.dp(4f))
        p.line(c, hx, top + p.dp(20f), hx, top + p.dp(20f) + len, 0xFFE0413A.toInt(), p.dp(20f))
        p.circle(c, hx, top + p.dp(20f) + len, p.dp(24f), 0xFFB7C6D6.toInt())
        p.circle(c, hx, top + p.dp(20f) + len, p.dp(13f), Palette.BLUE_DARK)
    }

    // ---- Stars ----
    fun starRow(p: Painter, c: Canvas, cx: Float, cy: Float, starSize: Float, earned: Int, total: Int = 3, gap: Float = 0f) {
        val g = if (gap > 0f) gap else starSize * 1.25f
        val totalW = (total - 1) * g
        var x = cx - totalW / 2f
        for (i in 0 until total) {
            star(p, c, x, cy, starSize / 2f, i < earned)
            x += g
        }
    }

    fun star(p: Painter, c: Canvas, cx: Float, cy: Float, r: Float, filled: Boolean) {
        val path = starPath(cx, cy, r, r * 0.46f)
        if (filled) {
            p.fillPath(c, path, Palette.AMBER)
            p.strokePath(c, path, 0xFFB8860B.toInt(), r * 0.12f)
        } else {
            p.fillPath(c, path, 0x33000000)
            p.strokePath(c, path, 0x66FFFFFF, r * 0.1f)
        }
    }

    private fun starPath(cx: Float, cy: Float, outer: Float, inner: Float): Path {
        val path = Path()
        for (i in 0 until 10) {
            val rad = if (i % 2 == 0) outer else inner
            val a = -Math.PI / 2 + i * Math.PI / 5
            val x = cx + (cos(a) * rad).toFloat()
            val y = cy + (sin(a) * rad).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    // ---- Weldrite solvent cement can (branding prop) ----
    fun cementCan(p: Painter, c: Canvas, cx: Float, cy: Float, scale: Float, glow: Boolean = false, bmp: Bitmap? = null) {
        if (bmp != null) {
            val maxW = 178f * scale; val maxH = 222f * scale
            if (glow) p.circle(c, cx, cy, maxW * 0.74f, 0x2242A5F5)
            p.image(c, bmp, cx, cy, maxW, maxH)
            return
        }
        val bw = 150f * scale; val bh = 188f * scale
        val body = RectF(cx - bw / 2, cy - bh / 2, cx + bw / 2, cy + bh / 2)
        if (glow) p.circle(c, cx, cy, bw * 0.9f, 0x2242A5F5)
        // Can body
        p.panel(c, body, Palette.BLUE, bw * 0.12f)
        p.rect(c, body.left + bw * 0.08f, body.top, body.left + bw * 0.2f, body.bottom, 0x33FFFFFF, bw * 0.05f)
        // White label band
        val label = RectF(body.left, cy - bh * 0.22f, body.right, cy + bh * 0.18f)
        p.rrect(c, label, Palette.WHITE, bw * 0.05f)
        p.rect(c, label.left, label.top, label.right, label.top + bh * 0.05f, Palette.RED)
        p.rect(c, label.left, label.bottom - bh * 0.05f, label.right, label.bottom, Palette.RED)
        // Brand text
        p.textCentered(c, "WELDRITE", cx, cy - bh * 0.04f, bw * 0.17f, Palette.BLUE_DARK)
        p.textCentered(c, "CPVC CEMENT", cx, cy + bh * 0.08f, bw * 0.085f, Palette.RED)
        // Red cap
        val cap = RectF(cx - bw * 0.22f, body.top - bh * 0.12f, cx + bw * 0.22f, body.top + bh * 0.02f)
        p.panel(c, cap, Palette.RED, bw * 0.06f, shadow = false)
        p.rect(c, cap.left, cap.top, cap.right, cap.top + bh * 0.03f, 0x55FFFFFF, bw * 0.04f)
    }

    // ---- Logo wordmark ----
    fun logo(p: Painter, c: Canvas, cx: Float, cy: Float, size: Float, bmp: Bitmap? = null) {
        if (bmp != null) {
            p.image(c, bmp, cx, cy, size * 7.4f, size * 2.0f)
            p.textCentered(c, "C P V C   M A S T E R", cx, cy + size * 1.15f, size * 0.34f, Palette.BLUE_LIGHT)
            return
        }
        // "WELDRITE" with a red CPVC-pipe accent crossing the W
        val w = p.textWidth("WELDRITE", size)
        p.text(c, "WELDRITE", cx, cy, size, Palette.WHITE)
        // red pipe underline with elbow
        val uy = cy + size * 0.18f
        p.line(c, cx - w / 2, uy, cx + w * 0.34f, uy, Palette.RED, size * 0.12f)
        p.line(c, cx + w * 0.34f, uy, cx + w / 2, uy - size * 0.16f, Palette.RED, size * 0.12f)
        p.textCentered(c, "C P V C   M A S T E R", cx, cy + size * 0.62f, size * 0.34f, Palette.BLUE_LIGHT)
    }

    // ---- Rank badge ----
    private val rankColors = intArrayOf(
        0xFF8D6E63.toInt(), // apprentice - bronze
        0xFF90A4AE.toInt(), // junior - steel
        0xFF42A5F5.toInt(), // technician - blue
        0xFF26C6DA.toInt(), // senior - cyan
        0xFFAB47BC.toInt(), // expert - purple
        0xFFFFC107.toInt(), // master - gold
    )

    fun rankBadge(p: Painter, c: Canvas, cx: Float, cy: Float, r: Float, rank: Rank) {
        val col = rankColors[rank.ordinal.coerceIn(0, rankColors.lastIndex)]
        // Shield
        val path = Path().apply {
            moveTo(cx - r, cy - r * 0.9f)
            lineTo(cx + r, cy - r * 0.9f)
            lineTo(cx + r, cy + r * 0.2f)
            quadTo(cx + r, cy + r, cx, cy + r * 1.25f)
            quadTo(cx - r, cy + r, cx - r, cy + r * 0.2f)
            close()
        }
        p.fillPath(c, path, col)
        p.strokePath(c, path, 0x55FFFFFF, r * 0.1f)
        // Wrench mark
        wrenchMark(p, c, cx, cy - r * 0.05f, r * 0.62f)
    }

    private fun wrenchMark(p: Painter, c: Canvas, cx: Float, cy: Float, s: Float) {
        val paintW = s * 0.34f
        p.line(c, cx - s * 0.5f, cy + s * 0.5f, cx + s * 0.35f, cy - s * 0.4f, Palette.WHITE, paintW)
        p.ring(c, cx + s * 0.45f, cy - s * 0.5f, s * 0.28f, paintW * 0.8f, Palette.WHITE)
        p.ring(c, cx - s * 0.55f, cy + s * 0.55f, s * 0.26f, paintW * 0.8f, Palette.WHITE)
    }
}
