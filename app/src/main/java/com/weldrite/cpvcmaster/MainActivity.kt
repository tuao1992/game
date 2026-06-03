package com.weldrite.cpvcmaster

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.weldrite.cpvcmaster.engine.Game
import com.weldrite.cpvcmaster.engine.GameHost
import com.weldrite.cpvcmaster.engine.GameView

/**
 * Single-activity host. Owns the [Game] and its [GameView]; wires Android
 * lifecycle to the game loop and the audio engine, and keeps the UI immersive.
 */
class MainActivity : Activity(), GameHost {

    private lateinit var game: Game
    private lateinit var view: GameView

    override fun exitToBackground() { moveTaskToBack(true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        game = Game(applicationContext, this)
        view = GameView(this, game)
        setContentView(view)
        applyImmersive()
    }

    private fun applyImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            view.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyImmersive()
    }

    override fun onResume() {
        super.onResume()
        game.audio.start()
        game.refreshSettings()
    }

    override fun onPause() {
        super.onPause()
        game.save.flush()
        game.audio.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        game.audio.release()
    }

    @Deprecated("Back is routed through the in-game screen stack")
    override fun onBackPressed() {
        view.queueBack()
    }
}
