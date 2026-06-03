package com.weldrite.cpvcmaster

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.core.app.ApplicationProvider
import com.weldrite.cpvcmaster.data.GameMode
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.GameHost
import com.weldrite.cpvcmaster.engine.Screen
import com.weldrite.cpvcmaster.engine.TouchEvent
import com.weldrite.cpvcmaster.screens.AchievementsScreen
import com.weldrite.cpvcmaster.screens.CareerMapScreen
import com.weldrite.cpvcmaster.screens.MenuScreen
import com.weldrite.cpvcmaster.screens.PlayScreen
import com.weldrite.cpvcmaster.screens.SettingsScreen
import com.weldrite.cpvcmaster.screens.SplashScreen
import com.weldrite.cpvcmaster.screens.TutorialScreen
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import kotlin.random.Random

/**
 * Headless crash-smoke: boots the Game and fuzzes touch input through every
 * screen and all gameplay phases against a real Bitmap-backed Canvas. The
 * assertion is simply that nothing throws across thousands of update/render
 * frames — exercising the rendering and state-machine code paths.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SmokeTest {

    private val W = 1080
    private val H = 1920
    private val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bmp)
    private val rng = Random(99)

    private fun newGame(): Game {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val game = Game(ctx, object : GameHost { override fun exitToBackground() {} })
        game.resize(W, H)            // boots SplashScreen + painter
        return game
    }

    /** Fuzz a screen for [frames] frames with randomized taps, drags and holds. */
    private fun drive(s: Screen, frames: Int) {
        s.layout()
        var fingerDown = false
        var fx = 0f; var fy = 0f; var hold = 0
        for (i in 0 until frames) {
            if (!fingerDown && rng.nextFloat() < 0.35f) {
                fingerDown = true
                // 45% biased toward the joint/fitting area to satisfy press-and-hold
                if (rng.nextFloat() < 0.45f) { fx = W * 0.74f + (rng.nextFloat() - 0.5f) * 160f; fy = H * 0.5f + (rng.nextFloat() - 0.5f) * 160f }
                else { fx = rng.nextFloat() * W; fy = H * (0.28f + rng.nextFloat() * 0.68f) }
                hold = 1 + rng.nextInt(220)   // long enough to complete holds sometimes
                s.onTouch(TouchEvent(TouchEvent.Kind.DOWN, fx, fy))
            } else if (fingerDown) {
                fx = (fx + (rng.nextFloat() - 0.5f) * 60f).coerceIn(0f, W.toFloat())
                fy = (fy + (rng.nextFloat() - 0.5f) * 60f).coerceIn(0f, H.toFloat())
                s.onTouch(TouchEvent(TouchEvent.Kind.MOVE, fx, fy))
                if (--hold <= 0) { s.onTouch(TouchEvent(TouchEvent.Kind.UP, fx, fy)); fingerDown = false }
            }
            s.update(0.016f)
            s.render(canvas)
        }
        if (fingerDown) s.onTouch(TouchEvent(TouchEvent.Kind.UP, fx, fy))
    }

    @Test fun boot_and_navigate_menus() {
        val game = newGame()
        // drive the live game (splash -> menu) for a while, tapping center
        repeat(240) { i ->
            if (i % 30 == 0) { game.onTouch(TouchEvent(TouchEvent.Kind.DOWN, W / 2f, H / 2f)); game.onTouch(TouchEvent(TouchEvent.Kind.UP, W / 2f, H / 2f)) }
            game.update(0.016f); game.render(canvas)
        }
    }

    @Test fun splash_renders() { drive(SplashScreen(newGame()), 200) }
    @Test fun menu_fuzz() { drive(MenuScreen(newGame()), 1500) }
    @Test fun career_map_fuzz() { drive(CareerMapScreen(newGame()), 1500) }
    @Test fun settings_fuzz() { drive(SettingsScreen(newGame()), 1500) }
    @Test fun achievements_fuzz() { drive(AchievementsScreen(newGame()), 1200) }
    @Test fun tutorial_fuzz() { drive(TutorialScreen(newGame()), 3000) }

    @Test fun career_play_fuzz() { drive(PlayScreen(newGame(), GameMode.CAREER, 0), 6000) }
    @Test fun career_play_hard_fuzz() { drive(PlayScreen(newGame(), GameMode.CAREER, 49), 6000) }
    @Test fun time_attack_fuzz() { drive(PlayScreen(newGame(), GameMode.TIME_ATTACK), 5000) }
    @Test fun endless_fuzz() { drive(PlayScreen(newGame(), GameMode.ENDLESS), 5000) }
}
