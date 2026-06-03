package com.weldrite.cpvcmaster.engine

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

/** Small math / easing helpers used throughout the game. */
object Geom {
    fun clamp(v: Float, a: Float, b: Float): Float = if (v < a) a else if (v > b) b else v
    fun clamp01(v: Float): Float = clamp(v, 0f, 1f)
    fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
    fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Float = hypot(x2 - x1, y2 - y1)

    fun smoothstep(t: Float): Float { val x = clamp01(t); return x * x * (3f - 2f * x) }
    fun easeOutCubic(t: Float): Float { val x = clamp01(t); return 1f - (1f - x).pow(3) }
    fun easeInCubic(t: Float): Float { val x = clamp01(t); return x * x * x }
    fun easeInOut(t: Float): Float { val x = clamp01(t); return if (x < 0.5f) 4f * x * x * x else 1f - (-2f * x + 2f).pow(3) / 2f }

    /** Overshooting ease for pop-in animations. */
    fun easeOutBack(t: Float): Float {
        val c1 = 1.70158f; val c3 = c1 + 1f; val x = clamp01(t)
        return 1f + c3 * (x - 1f).pow(3) + c1 * (x - 1f).pow(2)
    }

    fun pulse(timeSec: Float, hz: Float): Float = (sin(timeSec * hz * 2f * Math.PI.toFloat()) + 1f) / 2f
    fun cosWave(timeSec: Float, hz: Float): Float = cos(timeSec * hz * 2f * Math.PI.toFloat())
}
