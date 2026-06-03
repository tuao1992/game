package com.weldrite.cpvcmaster.engine

import android.content.Context
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Hosts the game loop on a dedicated thread. Touch events are captured on the UI
 * thread and queued; everything else (resize, input dispatch, update, render)
 * runs on the loop thread so game state stays single-threaded.
 */
class GameView(context: Context, val game: Game) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    @Volatile private var running = false
    private var thread: Thread? = null

    private val inputLock = Any()
    private val pending = ArrayList<TouchEvent>(32)

    @Volatile private var pendW = 0
    @Volatile private var pendH = 0
    @Volatile private var sizeDirty = false
    private var lastNanos = 0L

    init {
        holder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) = resumeLoop()
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        pendW = width; pendH = height; sizeDirty = true
    }
    override fun surfaceDestroyed(holder: SurfaceHolder) = pauseLoop()

    fun resumeLoop() {
        if (running) return
        running = true
        lastNanos = System.nanoTime()
        thread = Thread(this, "GameLoop").also { it.start() }
    }

    fun pauseLoop() {
        running = false
        try { thread?.join(800) } catch (_: InterruptedException) {}
        thread = null
        game.onPause()
    }

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val kind = when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> TouchEvent.Kind.DOWN
            MotionEvent.ACTION_MOVE -> TouchEvent.Kind.MOVE
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> TouchEvent.Kind.UP
            else -> return true
        }
        synchronized(inputLock) { pending.add(TouchEvent(kind, e.x, e.y)) }
        return true
    }

    /** Queued from the Activity's back handler. */
    fun queueBack() {
        synchronized(inputLock) { pending.add(TouchEvent(TouchEvent.Kind.BACK, 0f, 0f)) }
    }

    override fun run() {
        while (running) {
            val start = System.nanoTime()
            var dt = (start - lastNanos) / 1_000_000_000f
            lastNanos = start
            if (dt > 0.05f) dt = 0.05f else if (dt < 0f) dt = 0f

            if (sizeDirty) { sizeDirty = false; game.resize(pendW, pendH) }

            val batch: List<TouchEvent>
            synchronized(inputLock) {
                batch = if (pending.isEmpty()) emptyList() else ArrayList(pending).also { pending.clear() }
            }
            for (ev in batch) game.onTouch(ev)

            game.update(dt)

            // Prefer a hardware-accelerated canvas (API 23+); fall back to software.
            val surface = holder.surface
            var hardware = false
            var c = if (surface != null && surface.isValid) {
                try { hardware = true; surface.lockHardwareCanvas() } catch (_: Throwable) { hardware = false; null }
            } else null
            if (c == null) { hardware = false; c = try { holder.lockCanvas() } catch (_: Throwable) { null } }
            if (c != null) {
                try { game.render(c) } finally {
                    try { if (hardware) surface!!.unlockCanvasAndPost(c) else holder.unlockCanvasAndPost(c) } catch (_: Throwable) {}
                }
            }

            val elapsed = System.nanoTime() - start
            val sleepMs = (16_666_666L - elapsed) / 1_000_000L
            if (sleepMs > 1) try { Thread.sleep(sleepMs) } catch (_: InterruptedException) {}
        }
    }
}
