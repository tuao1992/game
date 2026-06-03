package com.weldrite.cpvcmaster

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View

// Temporary boot screen — replaced by the full game engine once the build is verified.
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(BootView(this))
    }
}

private class BootView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 72f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.parseColor("#0E2A47"))
        canvas.drawText("Weldrite CPVC Master", width / 2f, height / 2f, paint)
    }
}
