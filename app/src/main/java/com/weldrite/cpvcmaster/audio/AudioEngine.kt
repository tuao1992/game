package com.weldrite.cpvcmaster.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** Sound effect identifiers. The engine synthesizes each one — no audio files. */
enum class Sfx { CLICK, SELECT, CUT, CLEAN, DEBUR, CEMENT, ALIGN_LOCK, JOIN_SET, WATER, SUCCESS, PERFECT, LEAK, STAR, COUNT, ERROR }

/**
 * Tiny real-time software synth + mixer. A background thread streams 16-bit PCM
 * to an [AudioTrack], summing a list of short-lived [Voice]s. SFX add transient
 * voices; a step sequencer spawns notes for a gentle looping workshop track.
 */
class AudioEngine {

    private val sr = 22050
    private val rng = Random(1234)

    @Volatile var musicEnabled = true
    @Volatile var sfxEnabled = true
    @Volatile private var running = false

    private var track: AudioTrack? = null
    private var thread: Thread? = null
    private val voices = ArrayList<Voice>(64)

    // ---- Music sequencer state ----
    private var stepIndex = 0
    private var samplesToNextStep = 0
    private val bpm = 104
    private val stepLen get() = (sr * 60.0 / bpm / 2.0).toInt() // 8th notes

    fun start() {
        if (running) return
        val minBuf = AudioTrack.getMinBufferSize(sr, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val bufBytes = maxOf(minBuf, 4096 * 2)
        track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sr)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufBytes)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        running = true
        track?.play()
        thread = Thread({ loop() }, "AudioMixer").apply { priority = Thread.MAX_PRIORITY; start() }
    }

    fun release() {
        running = false
        try { thread?.join(400) } catch (_: InterruptedException) {}
        thread = null
        try { track?.stop() } catch (_: Exception) {}
        track?.release()
        track = null
        synchronized(voices) { voices.clear() }
    }

    fun play(sfx: Sfx) = play(sfx, 1f)

    /** Play an SFX with an optional [pitch] multiplier (e.g. rising with combos). */
    fun play(sfx: Sfx, pitch: Float) {
        if (!sfxEnabled || !running) return
        synchronized(voices) {
            val start = voices.size
            build(sfx)
            if (pitch != 1f) for (i in start until voices.size) voices[i].scaleHz(pitch)
        }
    }

    private fun loop() {
        val n = 1024
        val f = FloatArray(n)
        val s = ShortArray(n)
        val master = 0.85f
        while (running) {
            java.util.Arrays.fill(f, 0f)
            advanceMusic(n)
            if (musicEnabled) addAmbient(f, n)
            synchronized(voices) {
                var i = 0
                while (i < voices.size) {
                    val v = voices[i]
                    var k = 0
                    while (k < n) { f[k] += v.next(); k++ }
                    if (v.done) voices.removeAt(i) else i++
                }
            }
            var k = 0
            while (k < n) {
                var x = f[k] * master
                if (x > 1f) x = 1f else if (x < -1f) x = -1f
                s[k] = (x * 32767f).toInt().toShort()
                k++
            }
            try {
                if (running) track?.write(s, 0, n)
            } catch (_: Exception) {
                // track may have been released from another thread during shutdown
            }
        }
    }

    // ---- Music: pentatonic-ish loop over a I–vi–IV–V feel in A minor / C major ----
    // Each step: bass note + occasional melody note. Semitone offsets from a base.
    private val baseHz = 130.81 // C3
    private val bassPattern = intArrayOf(0, 0, 9, 9, 5, 5, 7, 7)          // C C A A F F G G (low)
    private val melody = intArrayOf(
        12, 16, 19, 16, 24, 21, 19, 16,
        9, 12, 16, 12, 17, 16, 14, 12,
    )

    private fun advanceMusic(n: Int) {
        var consumed = 0
        while (consumed < n) {
            if (samplesToNextStep <= 0) {
                if (musicEnabled) spawnStep(stepIndex, consumed)
                stepIndex = (stepIndex + 1) % melody.size
                samplesToNextStep += stepLen
            }
            val adv = min(n - consumed, samplesToNextStep)
            consumed += adv
            samplesToNextStep -= adv
        }
    }

    private fun spawnStep(step: Int, delay: Int) {
        synchronized(voices) {
            // Bass (every step), soft triangle
            val bassSemi = bassPattern[step % bassPattern.size] - 12
            voices += Voice(hz(bassSemi), 0.10f, stepLen + 600, WAVE_TRI, delay, 6, stepLen).also { it.detune = 0.6 }
            // Melody (every step), gentle square pluck
            val mSemi = melody[step % melody.size]
            voices += Voice(hz(mSemi), 0.075f, (stepLen * 0.9f).toInt() + 400, WAVE_TRI, delay, 4, (stepLen * 0.7f).toInt())
            // Light off-beat sparkle on some steps
            if (step % 4 == 2) voices += Voice(hz(mSemi + 12), 0.04f, 1800, WAVE_SINE, delay + stepLen / 2, 3, 1400)
        }
    }

    private fun hz(semi: Int): Double = baseHz * Math.pow(2.0, semi / 12.0)

    // Gentle, slowly-pulsing low pad — a "felt not heard" workshop soundscape.
    private var ambP1 = 0.0
    private var ambP2 = 0.0
    private var ambLfo = 0.0
    private fun addAmbient(f: FloatArray, n: Int) {
        val inc1 = 98.0 / sr       // G2
        val inc2 = 146.83 / sr     // D3 (a fifth up)
        var k = 0
        while (k < n) {
            ambP1 += inc1; if (ambP1 >= 1.0) ambP1 -= 1.0
            ambP2 += inc2; if (ambP2 >= 1.0) ambP2 -= 1.0
            ambLfo += 0.11 / sr; if (ambLfo >= 1.0) ambLfo -= 1.0
            val trem = 0.6f + 0.4f * ((sin(ambLfo * TWO_PI) + 1.0) * 0.5f).toFloat()
            f[k] += ((sin(ambP1 * TWO_PI) + 0.7 * sin(ambP2 * TWO_PI)).toFloat()) * 0.014f * trem
            k++
        }
    }

    // ---- SFX builders (added under `voices` lock) ----
    private fun build(sfx: Sfx) {
        when (sfx) {
            Sfx.CLICK -> voices += Voice(660.0, 0.28f, ms(60), WAVE_SINE, 0, ms(3), ms(50))
            Sfx.SELECT -> voices += Voice(880.0, 0.30f, ms(70), WAVE_TRI, 0, ms(3), ms(55))
            Sfx.COUNT -> voices += Voice(990.0, 0.32f, ms(80), WAVE_SINE, 0, ms(3), ms(60))
            Sfx.CUT -> { // gritty noise burst + tone
                voices += Voice(0.0, 0.40f, ms(120), WAVE_NOISE, 0, ms(2), ms(90))
                voices += Voice(220.0, 0.18f, ms(120), WAVE_SQUARE, 0, ms(2), ms(90))
            }
            Sfx.CLEAN -> voices += Voice(0.0, 0.18f, ms(90), WAVE_NOISE, 0, ms(8), ms(70))
            Sfx.DEBUR -> { voices += Voice(0.0, 0.22f, ms(70), WAVE_NOISE, 0, ms(2), ms(50)); voices += Voice(1500.0, 0.12f, ms(70), WAVE_SQUARE, 0, ms(2), ms(50)) }
            Sfx.CEMENT -> voices += Voice(330.0, 0.16f, ms(90), WAVE_SINE, 0, ms(10), ms(70))
            Sfx.ALIGN_LOCK -> { voices += Voice(740.0, 0.26f, ms(70), WAVE_TRI, 0, ms(3), ms(50)); voices += Voice(988.0, 0.26f, ms(90), WAVE_TRI, ms(70), ms(3), ms(60)) }
            Sfx.JOIN_SET -> { voices += Voice(150.0, 0.42f, ms(180), WAVE_SINE, 0, ms(4), ms(150)); voices += Voice(0.0, 0.20f, ms(60), WAVE_NOISE, 0, ms(2), ms(45)) }
            Sfx.WATER -> { voices += Voice(0.0, 0.22f, ms(420), WAVE_NOISE, 0, ms(60), ms(260)); voices += Voice(0.0, 0.10f, ms(420), WAVE_NOISE, ms(120), ms(80), ms(220)) }
            Sfx.SUCCESS -> arpeggio(intArrayOf(0, 4, 7), 523.25, 90, 0.30f, WAVE_TRI)
            Sfx.PERFECT -> arpeggio(intArrayOf(0, 4, 7, 12), 523.25, 85, 0.32f, WAVE_TRI)
            Sfx.STAR -> voices += Voice(1320.0, 0.30f, ms(120), WAVE_TRI, 0, ms(3), ms(90))
            Sfx.LEAK -> { voices += Voice(180.0, 0.34f, ms(420), WAVE_SQUARE, 0, ms(6), ms(300)).also { it.glide = -90.0 }; voices += Voice(0.0, 0.16f, ms(420), WAVE_NOISE, 0, ms(20), ms(280)) }
            Sfx.ERROR -> { voices += Voice(196.0, 0.30f, ms(200), WAVE_SQUARE, 0, ms(4), ms(150)); voices += Voice(185.0, 0.20f, ms(200), WAVE_SQUARE, ms(10), ms(4), ms(150)) }
        }
    }

    private fun arpeggio(semis: IntArray, rootHz: Double, gapMs: Int, amp: Float, wave: Int) {
        for ((i, s) in semis.withIndex()) {
            val f = rootHz * Math.pow(2.0, s / 12.0)
            voices += Voice(f, amp, ms(180), wave, ms(gapMs * i), ms(4), ms(140))
        }
    }

    private fun ms(m: Int): Int = (sr * m / 1000.0).toInt().coerceAtLeast(1)

    companion object {
        const val WAVE_SINE = 0
        const val WAVE_SQUARE = 1
        const val WAVE_TRI = 2
        const val WAVE_NOISE = 3
    }

    /** A single mono voice with linear attack/release envelope. */
    private inner class Voice(
        freq: Double,
        private val amp: Float,
        private val total: Int,
        private val wave: Int,
        delay: Int,
        attack: Int,
        releaseStart: Int,
    ) {
        private var phase = 0.0
        private var hz = freq
        private var delayLeft = delay
        private var left = total
        private val atk = attack.coerceAtLeast(1).toFloat()
        private val rel = (total - releaseStart).coerceAtLeast(1).toFloat()
        var detune = 0.0       // optional second-osc detune in Hz (adds warmth)
        var glide = 0.0        // Hz change per full duration (for falling tones)

        val done: Boolean get() = delayLeft <= 0 && left <= 0

        fun scaleHz(f: Float) { hz *= f }

        fun next(): Float {
            if (delayLeft > 0) { delayLeft--; return 0f }
            if (left <= 0) return 0f
            val pos = total - left
            val env = min(pos / atk, 1f) * min(left / rel, 1f)
            val osc = when (wave) {
                WAVE_SINE -> sin(phase * TWO_PI).toFloat()
                WAVE_SQUARE -> if (sin(phase * TWO_PI) >= 0) 1f else -1f
                WAVE_TRI -> (2.0 * kotlin.math.abs(2.0 * (phase - floor(phase + 0.5))) - 1.0).toFloat()
                else -> rng.nextFloat() * 2f - 1f
            }
            val inc = hz / sr
            phase += inc
            if (phase >= 1.0) phase -= 1.0
            if (glide != 0.0) hz += glide / total
            left--
            return osc * env * amp
        }
    }
}

private const val TWO_PI = 2.0 * PI
