package com.weldrite.cpvcmaster.engine

import android.graphics.Canvas
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Lightweight pooled particle system: bursts, directional sprays, confetti. */
class Particles {

    var densityScale = 1f
    private val rng = Random(System.nanoTime())

    private class P {
        var active = false
        var x = 0f; var y = 0f; var vx = 0f; var vy = 0f
        var life = 0f; var maxLife = 1f
        var size = 4f; var color = 0
        var gravity = 0f; var drag = 1f
        var square = false; var rot = 0f; var vr = 0f
    }

    private val pool = ArrayList<P>(256)

    val count: Int get() = pool.count { it.active }

    private fun obtain(): P {
        for (p in pool) if (!p.active) return p
        if (pool.size < 900) { val p = P(); pool.add(p); return p }
        return pool[0] // recycle oldest under pressure
    }

    fun clear() { for (p in pool) p.active = false }

    fun burst(x: Float, y: Float, n: Int, color: Int, speed: Float, size: Float, gravity: Float = 0f, life: Float = 0.7f) {
        val count = (n * densityScale).toInt().coerceAtLeast(1)
        repeat(count) {
            val a = rng.nextFloat() * 6.2832f
            val s = speed * (0.4f + rng.nextFloat())
            spawn(x, y, cos(a) * s, sin(a) * s, color, size, gravity, life)
        }
    }

    fun spray(x: Float, y: Float, dir: Float, spread: Float, n: Int, color: Int, speed: Float, size: Float, gravity: Float = 0f, life: Float = 0.6f) {
        val count = (n * densityScale).toInt().coerceAtLeast(1)
        repeat(count) {
            val a = dir + (rng.nextFloat() - 0.5f) * spread
            val s = speed * (0.5f + rng.nextFloat())
            spawn(x, y, cos(a) * s, sin(a) * s, color, size * (0.6f + rng.nextFloat() * 0.8f), gravity, life)
        }
    }

    fun confetti(x: Float, y: Float, n: Int, colors: IntArray) {
        val count = (n * densityScale).toInt().coerceAtLeast(1)
        repeat(count) {
            val a = -1.5708f + (rng.nextFloat() - 0.5f) * 2.2f
            val s = 600f + rng.nextFloat() * 900f
            val p = obtain()
            p.active = true; p.x = x; p.y = y
            p.vx = cos(a) * s; p.vy = sin(a) * s
            p.maxLife = 1.4f + rng.nextFloat(); p.life = p.maxLife
            p.size = 10f + rng.nextFloat() * 14f
            p.color = colors[rng.nextInt(colors.size)]
            p.gravity = 1400f; p.drag = 0.99f
            p.square = true; p.rot = rng.nextFloat() * 6.28f; p.vr = (rng.nextFloat() - 0.5f) * 16f
        }
    }

    private fun spawn(x: Float, y: Float, vx: Float, vy: Float, color: Int, size: Float, gravity: Float, life: Float) {
        val p = obtain()
        p.active = true; p.x = x; p.y = y; p.vx = vx; p.vy = vy
        p.maxLife = life; p.life = life; p.size = size; p.color = color
        p.gravity = gravity; p.drag = 0.985f; p.square = false; p.rot = 0f; p.vr = 0f
    }

    fun update(dt: Float) {
        for (p in pool) {
            if (!p.active) continue
            p.vy += p.gravity * dt
            p.vx *= p.drag; p.vy *= p.drag
            p.x += p.vx * dt; p.y += p.vy * dt
            p.rot += p.vr * dt
            p.life -= dt
            if (p.life <= 0f) p.active = false
        }
    }

    fun render(p: Painter, c: Canvas) {
        for (q in pool) {
            if (!q.active) continue
            val a = (q.life / q.maxLife).coerceIn(0f, 1f)
            val col = Painter.withAlpha(q.color, (a * 255).toInt())
            if (q.square) {
                c.save()
                c.rotate(Math.toDegrees(q.rot.toDouble()).toFloat(), q.x, q.y)
                p.rect(c, q.x - q.size / 2, q.y - q.size / 2, q.x + q.size / 2, q.y + q.size / 2, col, q.size * 0.2f)
                c.restore()
            } else {
                p.circle(c, q.x, q.y, q.size * (0.4f + a * 0.6f), col)
            }
        }
    }
}
