package com.weldrite.cpvcmaster.screens

import android.graphics.Canvas
import com.weldrite.cpvcmaster.data.GameMode
import com.weldrite.cpvcmaster.data.Loc
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.Geom
import com.weldrite.cpvcmaster.engine.Screen
import com.weldrite.cpvcmaster.engine.TouchEvent
import com.weldrite.cpvcmaster.gfx.Decor
import com.weldrite.cpvcmaster.ui.BtnStyle
import com.weldrite.cpvcmaster.ui.Button
import com.weldrite.cpvcmaster.ui.Palette

/** Main menu with rank summary, brand prop and navigation. */
class MenuScreen(game: Game) : Screen(game) {

    private var t = 0f

    init {
        listOf("play", "career", "time_attack", "endless", "achievements", "settings", "tutorial")
            .forEach { buttons.add(Button(it)) }
    }

    override fun layout() {
        val cx = w / 2f
        val bw = w * 0.72f
        val bh = p.dp(108f)
        val gap = p.dp(22f)
        var y = h * 0.40f
        fun place(id: String, height: Float = bh, width: Float = bw): Float {
            val b = buttons.first { it.id == id }
            b.set(cx - width / 2, y, cx + width / 2, y + height)
            y += height + gap
            return y
        }
        buttons.first { it.id == "play" }.style = BtnStyle.PRIMARY
        buttons.first { it.id == "play" }.label = Loc.t("play")
        place("play", bh * 1.12f)
        for (id in listOf("career", "time_attack", "endless")) {
            buttons.first { it.id == id }.style = BtnStyle.SECONDARY
            buttons.first { it.id == id }.label = Loc.t(id)
            place(id)
        }
        // bottom row: achievements | settings
        val half = (bw - gap) / 2f
        val ach = buttons.first { it.id == "achievements" }
        val set = buttons.first { it.id == "settings" }
        ach.style = BtnStyle.GHOST; set.style = BtnStyle.GHOST
        ach.label = Loc.t("achievements"); set.label = Loc.t("settings")
        ach.textScale = 0.32f; set.textScale = 0.36f
        ach.set(cx - bw / 2, y, cx - bw / 2 + half, y + bh * 0.82f)
        set.set(cx + bw / 2 - half, y, cx + bw / 2, y + bh * 0.82f)
        y += bh * 0.82f + gap
        val tut = buttons.first { it.id == "tutorial" }
        tut.style = BtnStyle.GHOST; tut.label = Loc.t("tutorial"); tut.textScale = 0.34f
        tut.set(cx - bw * 0.34f, y, cx + bw * 0.34f, y + bh * 0.7f)
    }

    override fun update(dt: Float) { t += dt; updateButtons(dt) }

    override fun onTouch(e: TouchEvent) {
        when (buttonsTouch(e)) {
            "play" -> startContinue()
            "career" -> game.push(CareerMapScreen(game))
            "time_attack" -> game.setRoot(PlayScreen(game, GameMode.TIME_ATTACK))
            "endless" -> game.setRoot(PlayScreen(game, GameMode.ENDLESS))
            "achievements" -> game.push(AchievementsScreen(game))
            "settings" -> game.push(SettingsScreen(game))
            "tutorial" -> game.push(TutorialScreen(game))
        }
    }

    private fun startContinue() {
        if (!game.save.tutorialDone) {
            game.push(TutorialScreen(game) { game.setRoot(PlayScreen(game, GameMode.CAREER, 0)) })
            return
        }
        val next = game.save.stars.indexOfFirst { it == 0 }
        if (next < 0) game.push(CareerMapScreen(game))
        else game.setRoot(PlayScreen(game, GameMode.CAREER, next))
    }

    override fun render(c: Canvas) {
        Decor.workshop(p, c, t)
        p.rect(c, 0f, 0f, w, h, 0x22000000)
        val cx = w / 2f

        Decor.logo(p, c, cx, h * 0.13f, p.dp(70f), bmp = game.images.logo())

        // Rank summary panel
        val rank = game.save.rank
        val py = h * 0.235f
        Decor.rankBadge(p, c, cx - w * 0.28f, py, p.dp(40f), rank)
        p.text(c, rank.title, cx - w * 0.18f, py - p.dp(6f), p.dp(34f), Palette.WHITE, android.graphics.Paint.Align.LEFT)
        p.text(c, "★ ${game.save.totalStars}   •   ${game.save.completedLevels}/${game.save.stars.size} ${Loc.t("level")}s",
            cx - w * 0.18f, py + p.dp(34f), p.dp(26f), Palette.OFFWHITE, android.graphics.Paint.Align.LEFT, bold = false)

        // Daily streak chip, top-right
        if (game.save.dailyStreak >= 1) {
            val r = android.graphics.RectF(w * 0.60f, h * 0.05f, w * 0.95f, h * 0.05f + p.dp(58f))
            p.panel(c, r, Palette.PANEL, p.dp(28f))
            flame(c, r.left + p.dp(36f), r.centerY(), p.dp(20f))
            val days = game.save.dailyStreak
            p.text(c, "$days day${if (days == 1) "" else "s"}", r.left + p.dp(64f), r.centerY() + p.dp(9f), p.dp(24f), Palette.WHITE, android.graphics.Paint.Align.LEFT, bold = false)
        }

        // Cement can prop, lower-right
        Decor.cementCan(p, c, w * 0.83f, h * 0.88f, p.dp(0.92f), bmp = game.images.cementCan())

        drawButtons(c)

        p.text(c, Loc.t("slogan2"), cx, h * 0.975f, p.dp(24f), Palette.BLUE_LIGHT, alpha = 200)
    }

    private fun flame(c: android.graphics.Canvas, cx: Float, cy: Float, r: Float) {
        val outer = p.path { pa ->
            pa.moveTo(cx, cy - r * 1.3f)
            pa.quadTo(cx + r, cy - r * 0.2f, cx + r * 0.8f, cy + r * 0.4f)
            pa.quadTo(cx + r * 0.8f, cy + r * 1.2f, cx, cy + r * 1.2f)
            pa.quadTo(cx - r * 0.8f, cy + r * 1.2f, cx - r * 0.8f, cy + r * 0.4f)
            pa.quadTo(cx - r, cy - r * 0.2f, cx, cy - r * 1.3f)
            pa.close()
        }
        p.fillPath(c, outer, Palette.RED)
        val inner = p.path { pa ->
            pa.moveTo(cx, cy - r * 0.5f)
            pa.quadTo(cx + r * 0.5f, cy + r * 0.1f, cx + r * 0.35f, cy + r * 0.6f)
            pa.quadTo(cx, cy + r * 0.9f, cx - r * 0.35f, cy + r * 0.6f)
            pa.quadTo(cx - r * 0.5f, cy + r * 0.1f, cx, cy - r * 0.5f)
            pa.close()
        }
        p.fillPath(c, inner, Palette.AMBER)
    }

    override fun onBack(): Boolean { game.host.exitToBackground(); return true }
}
