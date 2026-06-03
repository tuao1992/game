package com.weldrite.cpvcmaster.screens

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.weldrite.cpvcmaster.audio.Sfx
import com.weldrite.cpvcmaster.data.Achievement
import com.weldrite.cpvcmaster.data.Loc
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.Screen
import com.weldrite.cpvcmaster.engine.TouchEvent
import com.weldrite.cpvcmaster.ui.BtnStyle
import com.weldrite.cpvcmaster.ui.Button
import com.weldrite.cpvcmaster.ui.Palette
import kotlin.math.abs

/** Scrollable achievements list with unlock state and progress. */
class AchievementsScreen(game: Game) : Screen(game) {

    private val list = Achievement.entries
    private val stats = game.save.stats()
    private val back = Button("back").apply { style = BtnStyle.GHOST; label = Loc.t("back"); textScale = 0.36f }

    private var scrollY = 0f
    private var maxScroll = 0f
    private var velY = 0f
    private var top0 = 0f
    private var rowH = 0f
    private var downY = 0f
    private var startScroll = 0f
    private var dragging = false

    override fun layout() {
        back.set(p.dp(24f), p.dp(34f), p.dp(24f) + p.dp(150f), p.dp(34f) + p.dp(72f))
        top0 = h * 0.17f
        rowH = p.dp(160f)
        val gap = p.dp(18f)
        val contentH = top0 + list.size * (rowH + gap) + p.dp(40f)
        maxScroll = (contentH - h).coerceAtLeast(0f)
    }

    private fun rowRect(i: Int): RectF {
        val gap = p.dp(18f)
        val y = top0 + i * (rowH + gap) - scrollY
        return RectF(w * 0.06f, y, w * 0.94f, y + rowH)
    }

    override fun update(dt: Float) {
        if (!dragging && abs(velY) > 1f) {
            scrollY = (scrollY + velY * dt).coerceIn(0f, maxScroll); velY *= 0.9f
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
            TouchEvent.Kind.UP -> { if (!dragging && back.onUp(e.x, e.y)) { game.audio.play(Sfx.CLICK); game.pop() }; back.cancel(); dragging = false }
            else -> {}
        }
    }

    override fun render(c: Canvas) {
        p.backdrop(c, 0xFF13486F.toInt(), Palette.BLUE_DARK)
        c.save(); c.clipRect(0f, top0 - p.dp(8f), w, h)
        val unlockedCount = list.count { it.isUnlocked(stats) }
        for (i in list.indices) {
            val r = rowRect(i)
            if (r.bottom < top0 || r.top > h) continue
            drawRow(c, list[i], r)
        }
        c.restore()
        p.rect(c, 0f, 0f, w, top0 - p.dp(8f), Palette.BLUE_DARK)
        p.rect(c, 0f, top0 - p.dp(10f), w, top0 - p.dp(6f), 0x33FFFFFF)
        back.draw(p, c)
        p.text(c, Loc.t("achievements"), w / 2f, p.dp(96f), p.dp(46f), Palette.WHITE)
        p.text(c, "$unlockedCount / ${list.size}", w / 2f, p.dp(140f), p.dp(28f), Palette.AMBER, bold = false)
    }

    private fun drawRow(c: Canvas, a: Achievement, r: RectF) {
        val unlocked = a.isUnlocked(stats)
        p.panel(c, r, if (unlocked) 0xFF1C4E78.toInt() else Palette.PANEL, p.dp(28f))
        // Medal
        val mcx = r.left + p.dp(80f); val mcy = r.centerY()
        val mr = p.dp(52f)
        p.circle(c, mcx, mcy, mr, if (unlocked) Palette.AMBER else 0xFF3A536B.toInt())
        p.ring(c, mcx, mcy, mr, p.dp(5f), if (unlocked) 0xFFB8860B.toInt() else 0x44FFFFFF)
        if (unlocked) {
            // check mark
            p.line(c, mcx - mr * 0.4f, mcy, mcx - mr * 0.05f, mcy + mr * 0.35f, Palette.WHITE, p.dp(8f))
            p.line(c, mcx - mr * 0.05f, mcy + mr * 0.35f, mcx + mr * 0.45f, mcy - mr * 0.35f, Palette.WHITE, p.dp(8f))
        } else {
            p.textCentered(c, "${(a.progress(stats) * 100).toInt()}%", mcx, mcy, p.dp(26f), Palette.WHITE)
        }
        // Texts
        val tx = mcx + mr + p.dp(34f)
        p.text(c, a.title, tx, r.top + p.dp(58f), p.dp(38f), if (unlocked) Palette.WHITE else Palette.OFFWHITE, Paint.Align.LEFT)
        p.text(c, a.desc, tx, r.top + p.dp(100f), p.dp(27f), Palette.MUTED, Paint.Align.LEFT, bold = false)
        // Progress bar
        if (!unlocked) {
            val barL = tx; val barR = r.right - p.dp(34f); val by = r.bottom - p.dp(34f)
            p.rect(c, barL, by, barR, by + p.dp(14f), 0x33000000, p.dp(7f))
            p.rect(c, barL, by, barL + (barR - barL) * a.progress(stats), by + p.dp(14f), Palette.BLUE_LIGHT, p.dp(7f))
        } else {
            p.text(c, "Unlocked", r.right - p.dp(34f), r.bottom - p.dp(28f), p.dp(26f), Palette.GREEN, Paint.Align.RIGHT, bold = false)
        }
    }

    override fun onBack(): Boolean { game.pop(); return true }
}
