package com.weldrite.cpvcmaster.engine

import android.graphics.Canvas

/**
 * Floating text popups for amplified, success-dependent feedback
 * ("PERFECT", "+150", "COMBO x3"). Each rises and fades with an overshoot pop-in.
 */
class Popups {

    private class T {
        var active = false
        var text = ""
        var x = 0f; var y = 0f; var vy = 0f
        var age = 0f; var life = 1f
        var size = 40f; var color = 0
    }

    private val pool = ArrayList<T>(24)

    fun clear() { for (t in pool) t.active = false }

    fun add(text: String, x: Float, y: Float, color: Int, size: Float, life: Float = 1.1f, rise: Float = -120f) {
        val t = pool.firstOrNull { !it.active } ?: T().also { pool.add(it) }
        t.active = true; t.text = text; t.x = x; t.y = y; t.vy = rise
        t.age = 0f; t.life = life; t.size = size; t.color = color
    }

    fun update(dt: Float) {
        for (t in pool) {
            if (!t.active) continue
            t.y += t.vy * dt
            t.vy *= (1f - (dt * 2.2f).coerceAtMost(1f))
            t.age += dt
            if (t.age >= t.life) t.active = false
        }
    }

    fun render(p: Painter, c: Canvas) {
        for (t in pool) {
            if (!t.active) continue
            val f = t.age / t.life
            val pop = Geom.easeOutBack((t.age / 0.22f).coerceIn(0f, 1f))
            val alpha = (255 * (1f - Geom.easeInCubic(((f - 0.6f) / 0.4f).coerceIn(0f, 1f)))).toInt().coerceIn(0, 255)
            val size = t.size * (0.6f + pop * 0.4f)
            // soft shadow for contrast on busy backgrounds
            p.textCentered(c, t.text, t.x + p.dp(2f), t.y + p.dp(2f), size, Painter.withAlpha(0xFF000000.toInt(), (alpha * 0.5f).toInt()))
            p.textCentered(c, t.text, t.x, t.y, size, Painter.withAlpha(t.color, alpha))
        }
    }
}
