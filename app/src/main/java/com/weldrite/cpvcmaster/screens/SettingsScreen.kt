package com.weldrite.cpvcmaster.screens

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.weldrite.cpvcmaster.audio.Sfx
import com.weldrite.cpvcmaster.data.Language
import com.weldrite.cpvcmaster.data.Loc
import com.weldrite.cpvcmaster.data.Quality
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.Screen
import com.weldrite.cpvcmaster.engine.TouchEvent
import com.weldrite.cpvcmaster.ui.BtnStyle
import com.weldrite.cpvcmaster.ui.Button
import com.weldrite.cpvcmaster.ui.Palette
import com.weldrite.cpvcmaster.ui.Toggle

/** Settings: music, sound, haptics, language and graphics quality. */
class SettingsScreen(game: Game) : Screen(game) {

    private val rows = listOf("music", "sound", "haptics", "language", "quality")
    private val rowRects = HashMap<String, RectF>()
    private val music = Toggle("music")
    private val sfx = Toggle("sound")
    private val haptics = Toggle("haptics")
    private val back = Button("back").apply { style = BtnStyle.SECONDARY }
    private val langBtn = Button("language").apply { style = BtnStyle.PRIMARY; textScale = 0.34f }
    private val qualityBtn = Button("quality").apply { style = BtnStyle.PRIMARY; textScale = 0.34f }

    init {
        music.on = game.save.musicOn
        sfx.on = game.save.sfxOn
        haptics.on = game.save.hapticsOn
        buttons.add(back); buttons.add(langBtn); buttons.add(qualityBtn)
    }

    override fun layout() {
        val left = w * 0.08f; val right = w * 0.92f
        val rowH = p.dp(118f); val gap = p.dp(20f)
        var y = h * 0.22f
        for (id in rows) {
            rowRects[id] = RectF(left, y, right, y + rowH)
            val cy = y + rowH / 2f
            when (id) {
                "music" -> music.set(right - p.dp(170f), cy - p.dp(35f), right - p.dp(30f), cy + p.dp(35f))
                "sound" -> sfx.set(right - p.dp(170f), cy - p.dp(35f), right - p.dp(30f), cy + p.dp(35f))
                "haptics" -> haptics.set(right - p.dp(170f), cy - p.dp(35f), right - p.dp(30f), cy + p.dp(35f))
                "language" -> langBtn.set(right - p.dp(300f), cy - p.dp(42f), right - p.dp(28f), cy + p.dp(42f))
                "quality" -> qualityBtn.set(right - p.dp(300f), cy - p.dp(42f), right - p.dp(28f), cy + p.dp(42f))
            }
            y += rowH + gap
        }
        back.set(w / 2f - p.dp(180f), y + p.dp(20f), w / 2f + p.dp(180f), y + p.dp(20f) + p.dp(100f))
        refreshLabels()
    }

    private fun refreshLabels() {
        back.label = Loc.t("back")
        langBtn.label = game.save.language.displayName
        qualityBtn.label = game.save.quality.displayName
    }

    override fun update(dt: Float) { updateButtons(dt); music.update(dt); sfx.update(dt); haptics.update(dt) }

    override fun onTouch(e: TouchEvent) {
        if (e.kind == TouchEvent.Kind.DOWN) {
            when {
                music.contains(e.x, e.y) -> { music.on = !music.on; game.save.setMusic(music.on); game.refreshSettings(); click(); return }
                sfx.contains(e.x, e.y) -> { sfx.on = !sfx.on; game.save.setSfx(sfx.on); game.refreshSettings(); click(); return }
                haptics.contains(e.x, e.y) -> { haptics.on = !haptics.on; game.save.setHaptics(haptics.on); game.refreshSettings(); game.haptics.light(); click(); return }
            }
        }
        when (buttonsTouch(e)) {
            "language" -> { val next = Language.entries[(game.save.language.ordinal + 1) % Language.entries.size]; game.save.applyLanguage(next); game.refreshSettings(); layout() }
            "quality" -> { val next = Quality.entries[(game.save.quality.ordinal + 1) % Quality.entries.size]; game.save.applyQuality(next); game.refreshSettings(); refreshLabels() }
            "back" -> game.pop()
        }
    }

    private fun click() { game.audio.play(Sfx.SELECT) }

    override fun render(c: Canvas) {
        p.backdrop(c, 0xFF13486F.toInt(), Palette.BLUE_DARK)
        p.text(c, Loc.t("settings"), w / 2f, h * 0.13f, p.dp(54f), Palette.WHITE)
        for (id in rows) {
            val r = rowRects[id] ?: continue
            p.panel(c, r, Palette.PANEL, p.dp(26f))
            p.text(c, Loc.t(id), r.left + p.dp(34f), r.centerY() + p.dp(14f), p.dp(38f), Palette.WHITE, Paint.Align.LEFT)
        }
        music.draw(p, c); sfx.draw(p, c); haptics.draw(p, c)
        drawButtons(c)
    }

    override fun onBack(): Boolean { game.pop(); return true }
}
