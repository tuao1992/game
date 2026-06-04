package com.weldrite.cpvcmaster.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Loads bundled brand assets (the Weldrite logo and product imagery) from
 * `assets/weldrite/`. Decoding is lazy, cached and downsampled for memory; any
 * failure returns null so callers fall back to the code-drawn rendering and the
 * game never crashes on a missing/undecodable asset.
 */
class Images(private val context: Context) {

    private val cache = HashMap<String, Bitmap?>()

    fun logo(): Bitmap? = get("logo.png", 1100)
    fun cementCan(): Bitmap? = get("cement_can.png", 700)

    @Synchronized
    fun get(name: String, maxDim: Int): Bitmap? {
        if (cache.containsKey(name)) return cache[name]
        var result: Bitmap? = null
        try {
            val bytes = context.assets.open("weldrite/$name").use { it.readBytes() }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            var sample = 1
            val longest = maxOf(bounds.outWidth, bounds.outHeight)
            while (longest > 0 && longest / sample > maxDim) sample *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
            if (bmp != null && bmp.width > 0 && bmp.height > 0) result = bmp
        } catch (_: Throwable) {
            result = null
        }
        cache[name] = result
        return result
    }
}
