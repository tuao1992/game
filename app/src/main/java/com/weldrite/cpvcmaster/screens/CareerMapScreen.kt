package com.weldrite.cpvcmaster.screens

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.weldrite.cpvcmaster.audio.Sfx
import com.weldrite.cpvcmaster.data.GameMode
import com.weldrite.cpvcmaster.data.Levels
import com.weldrite.cpvcmaster.data.Loc
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.Painter
import com.weldrite.cpvcmaster.engine.Screen
import com.weldrite.cpvcmaster.engine.TouchEvent
import com.weldrite.cpvcmaster.gfx.Decor
import com.weldrite.cpvcmaster.ui.BtnStyle
import com.weldrite.cpvcmaster.ui.Button
import com.weldrite.cpvcmaster.ui.Palette
import kotlin.math.abs
import kotlin.math.ceil

/** Scrollable grid of the 50 career levels with stars and lock state. */
class CareerMapScreen(game: Game) : Screen(game) {

    private val levels = Levels.all
    private val back = Button("back").apply { style = BtnStyle.GHOST; label = Loc.t("back"); textScale = 0.36f }

    private var scrollY = 0f
    private var maxScroll = 0f
    private var velY = 0f
    private val cols = 4
    private var tile = 0f
    private var gap = 0f
    private var side = 0f
    private var top0 = 0f

    private var downY = 0f
    private var startScroll = 0f
    private var dragging = false

    override fun layout() {
        back.set(p.dp(24f), p.dp(34f), p.dp(24f) + p.dp(150f), p.dp(34f) + p.dp(72f))
        gap = p.dp(26f)
        side = p.dp(36f)
        tile = (w - side * 2 - gap * (cols - 1)) / cols
        top0 = h * 0.17f
        val rows = ceil(levels.size / cols.toFloat()).toInt()
        val contentH = top0 + rows * (tile + gap) + p.dp(40f)
        maxScroll = (contentH - h).coerceAtLeast(0f)
        scrollY = scrollY.coerceIn(0f, maxScroll)
    }

    private fun tileRect(i: Int): RectF {
        val col = i % cols; val row = i / cols
        val x = side + col * (tile + gap)
        val y = top0 + row * (tile + gap) - scrollY
        return RectF(x, y, x + tile, y + tile)
    }

    override fun update(dt: Float) {
        if (!dragging && abs(velY) > 1f) {
            scrollY = (scrollY + velY * dt).coerceIn(0f, maxScroll)
            velY *= 0.90f
            if (scrollY <= 0f || scrollY >= maxScroll) velY = 0f
        }
        back.update(dt)
    }

    override fun onTouch(e: TouchEvent) {
        when (e.kind) {
            TouchEvent.Kind.DOWN -> { downY = e.y; startScroll = scrollY; dragging = false; velY = 0f; back.onDown(e.x, e.y) }
            TouchEvent.Kind.MOVE -> {
                val dy = e.y - downY
                if (abs(dy) > p.dp(12f)) { dragging = true; back.cancel() }
                if (dragging) { val ns = (startScroll - dy).coerceIn(0f, maxScroll); velY = (ns - scrollY) * 12f; scrollY = ns }
            }
            TouchEvent.Kind.UP -> {
                if (!dragging) {
                    if (back.onUp(e.x, e.y)) { game.audio.play(Sfx.CLICK); game.pop(); return }
                    for (i in levels.indices) {
                        if (tileRect(i).contains(e.x, e.y)) {
                            if (game.save.isLevelUnlocked(i)) { game.audio.play(Sfx.SELECT); game.setRoot(PlayScreen(game, GameMode.CAREER, i)) }
                            else { game.audio.play(Sfx.ERROR); game.haptics.error() }
                            break
                        }
                    }
                }
                back.cancel(); dragging = false
            }
            else -> {}
        }
    }

    override fun render(c: Canvas) {
        p.backdrop(c, 0xFF13486F.toInt(), Palette.BLUE_DARK)

        // Grid (clipped below header)
        c.save()
        c.clipRect(0f, top0 - p.dp(8f), w, h)
        for (i in levels.indices) {
            val r = tileRect(i)
            if (r.bottom < top0 - p.dp(10f) || r.top > h + p.dp(10f)) continue
            drawTile(c, i, r)
        }
        c.restore()

        // Header
        p.rect(c, 0f, 0f, w, top0 - p.dp(8f), Palette.BLUE_DARK)
        p.rect(c, 0f, top0 - p.dp(10f), w, top0 - p.dp(6f), 0x33FFFFFF)
        back.draw(p, c)
        p.text(c, Loc.t("career"), w / 2f, p.dp(96f), p.dp(46f), Palette.WHITE)
        p.text(c, "★ ${game.save.totalStars} / ${levels.size * 3}", w / 2f, p.dp(140f), p.dp(28f), Palette.AMBER, bold = false)
    }

    private fun drawTile(c: Canvas, i: Int, r: RectF) {
        val lvl = levels[i]
        val unlocked = game.save.isLevelUnlocked(i)
        val stars = game.save.stars[i]
        val base = if (unlocked) lvl.environment.accent else 0xFF2A4257.toInt()
        p.panel(c, r, base, tile * 0.18f, shadow = unlocked)
        p.border(c, RectF(r.left + 2, r.top + 2, r.right - 2, r.bottom - 2), 0x33FFFFFF, p.dp(2f), tile * 0.18f)

        if (unlocked) {
            p.textCentered(c, "${lvl.number}", r.centerX(), r.centerY() - tile * 0.08f, tile * 0.42f, Palette.WHITE)
            Decor.starRow(p, c, r.centerX(), r.bottom - tile * 0.18f, tile * 0.2f, stars, 3, tile * 0.24f)
            if (stars == 0) p.textCentered(c, "NEW", r.centerX(), r.top + tile * 0.2f, tile * 0.16f, Palette.AMBER)
        } else {
            p.textCentered(c, "${lvl.number}", r.centerX(), r.centerY() - tile * 0.06f, tile * 0.32f, 0x55FFFFFF)
            lock(c, r.centerX(), r.centerY() + tile * 0.18f, tile * 0.16f)
        }
    }

    private fun lock(c: Canvas, cx: Float, cy: Float, s: Float) {
        p.rect(c, cx - s, cy - s * 0.2f, cx + s, cy + s * 1.1f, 0xCCFFFFFF.toInt(), s * 0.25f)
        p.ring(c, cx, cy - s * 0.2f, s * 0.62f, s * 0.34f, 0xCCFFFFFF.toInt())
        p.circle(c, cx, cy + s * 0.4f, s * 0.18f, Palette.BLUE_DARK)
    }
}
