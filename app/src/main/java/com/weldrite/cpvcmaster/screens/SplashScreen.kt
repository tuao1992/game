package com.weldrite.cpvcmaster.screens

import android.graphics.Canvas
import com.weldrite.cpvcmaster.data.Loc
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.Geom
import com.weldrite.cpvcmaster.engine.Screen
import com.weldrite.cpvcmaster.engine.TouchEvent
import com.weldrite.cpvcmaster.gfx.Decor
import com.weldrite.cpvcmaster.ui.Palette

/** Animated brand splash: workshop scene, rising cement can, popping logo, slogan. */
class SplashScreen(game: Game) : Screen(game) {

    private var t = 0f
    private val dur = 2.6f
    private var advanced = false

    override fun update(dt: Float) {
        t += dt
        if (t >= dur) advance()
    }

    override fun onTouch(e: TouchEvent) {
        if (e.kind == TouchEvent.Kind.UP && t > 0.5f) advance()
    }

    private fun advance() {
        if (advanced) return
        advanced = true
        game.setRoot(MenuScreen(game))
    }

    override fun render(c: Canvas) {
        Decor.workshop(p, c, t)
        p.rect(c, 0f, 0f, w, h, 0x33000000)
        val cx = w / 2f

        val rise = Geom.easeOutCubic((t / 0.8f).coerceIn(0f, 1f))
        val canY = h * 0.40f + (1f - rise) * h * 0.18f
        Decor.cementCan(p, c, cx, canY, p.dp(1.85f), glow = true, bmp = game.images.cementCan())

        val ls = Geom.easeOutBack(((t - 0.5f) / 0.7f).coerceIn(0f, 1f))
        if (ls > 0f) {
            c.save(); c.scale(ls, ls, cx, h * 0.63f)
            Decor.logo(p, c, cx, h * 0.63f, p.dp(82f), bmp = game.images.logo())
            c.restore()
        }

        val sa = Geom.clamp01((t - 1.3f) / 0.6f)
        if (sa > 0f) p.text(c, Loc.t("slogan1"), cx, h * 0.78f, p.dp(36f), Palette.WHITE, alpha = (sa * 255).toInt())

        if (t > dur * 0.55f) {
            val a = (Geom.pulse(t, 1.1f) * 170 + 60).toInt().coerceIn(0, 255)
            p.text(c, Loc.t("tap_to_start"), cx, h * 0.9f, p.dp(28f), Palette.OFFWHITE, alpha = a)
        }
    }

    override fun onBack(): Boolean = true
}
