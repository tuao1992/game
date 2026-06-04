package com.weldrite.cpvcmaster.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local persistence backed by SharedPreferences (single JSON blob).
 * Holds settings, per-level stars, lifetime stats and achievement state.
 * All mutations are kept in memory and written via [flush]; settings flush eagerly.
 */
class SaveManager(context: Context) {

    private val prefs = context.getSharedPreferences("weldrite_save", Context.MODE_PRIVATE)
    private val levelCount = Levels.all.size

    // Settings
    var musicOn = true
    var sfxOn = true
    var hapticsOn = true
    var language = Language.EN
    var quality = Quality.MEDIUM

    // Progress
    val stars = IntArray(levelCount)
    private var leakFreeMask = 0L

    // Lifetime stats
    var totalJoints = 0
    var perfectJoints = 0
    var bestTimeAttack = 0
    var bestEndless = 0
    var tutorialDone = false
    var dailyStreak = 0
    private var lastPlayDay = 0L

    private val seenAchievements = linkedSetOf<String>()

    init { load() }

    // ---- Derived ----
    val totalStars get() = stars.sum()
    val threeStarLevels get() = stars.count { it == 3 }
    val completedLevels get() = stars.count { it >= 1 }
    val leakFreeLevels get() = java.lang.Long.bitCount(leakFreeMask)
    val rank: Rank get() = Rank.forStars(totalStars)

    fun stats() = Stats(
        totalJoints = totalJoints,
        perfectJoints = perfectJoints,
        leakFreeLevels = leakFreeLevels,
        bestTimeAttack = bestTimeAttack,
        bestEndless = bestEndless,
        threeStarLevels = threeStarLevels,
        completedLevels = completedLevels,
        totalStars = totalStars,
    )

    /** Career level [i] is unlocked if it's the first, or the previous has >=1 star. */
    fun isLevelUnlocked(i: Int): Boolean = i == 0 || (i in 1 until levelCount && stars[i - 1] >= 1)

    // ---- Recording ----
    fun recordJoint(perfect: Boolean) {
        totalJoints++
        if (perfect) perfectJoints++
    }

    fun recordCareerResult(levelIndex: Int, starsEarned: Int, leakFree: Boolean) {
        if (levelIndex in 0 until levelCount && starsEarned > stars[levelIndex]) {
            stars[levelIndex] = starsEarned
        }
        if (leakFree && levelIndex in 0 until levelCount) {
            leakFreeMask = leakFreeMask or (1L shl levelIndex)
        }
        flush()
    }

    fun recordTimeAttack(joints: Int) {
        if (joints > bestTimeAttack) bestTimeAttack = joints
        flush()
    }

    fun recordEndless(joints: Int) {
        if (joints > bestEndless) bestEndless = joints
        flush()
    }

    /** Returns achievements unlocked since last check (for celebratory popups). */
    fun pollNewAchievements(): List<Achievement> {
        val now = Achievement.unlocked(stats())
        val fresh = now.filter { it.id !in seenAchievements }
        if (fresh.isNotEmpty()) {
            fresh.forEach { seenAchievements += it.id }
            flush()
        }
        return fresh
    }

    fun setMusic(on: Boolean) { musicOn = on; flush() }
    fun setSfx(on: Boolean) { sfxOn = on; flush() }
    fun setHaptics(on: Boolean) { hapticsOn = on; flush() }
    fun applyLanguage(l: Language) { language = l; Loc.language = l; flush() }
    fun applyQuality(q: Quality) { quality = q; flush() }
    fun markTutorialDone() { tutorialDone = true; flush() }

    /** Updates the daily-play streak. Call once per app launch. */
    fun touchDailyStreak() {
        val day = System.currentTimeMillis() / 86_400_000L
        when {
            day == lastPlayDay -> {}
            day == lastPlayDay + 1 -> dailyStreak++
            else -> dailyStreak = 1
        }
        lastPlayDay = day
        flush()
    }

    // ---- Persistence ----
    private fun load() {
        val raw = prefs.getString("data", null) ?: run { Loc.language = language; return }
        try {
            val o = JSONObject(raw)
            musicOn = o.optBoolean("music", true)
            sfxOn = o.optBoolean("sfx", true)
            hapticsOn = o.optBoolean("haptics", true)
            language = runCatching { Language.valueOf(o.optString("lang", "EN")) }.getOrDefault(Language.EN)
            quality = runCatching { Quality.valueOf(o.optString("quality", "MEDIUM")) }.getOrDefault(Quality.MEDIUM)
            totalJoints = o.optInt("totalJoints")
            perfectJoints = o.optInt("perfectJoints")
            bestTimeAttack = o.optInt("bestTA")
            bestEndless = o.optInt("bestEndless")
            leakFreeMask = o.optLong("leakFreeMask")
            tutorialDone = o.optBoolean("tutorialDone")
            dailyStreak = o.optInt("streak")
            lastPlayDay = o.optLong("lastDay")
            o.optJSONArray("stars")?.let { arr ->
                for (i in 0 until minOf(arr.length(), levelCount)) stars[i] = arr.optInt(i)
            }
            o.optJSONArray("seenAch")?.let { arr ->
                for (i in 0 until arr.length()) seenAchievements += arr.optString(i)
            }
        } catch (_: Exception) {
            // Corrupt save — start fresh rather than crash.
        }
        Loc.language = language
    }

    fun flush() {
        val o = JSONObject()
        o.put("music", musicOn)
        o.put("sfx", sfxOn)
        o.put("haptics", hapticsOn)
        o.put("lang", language.name)
        o.put("quality", quality.name)
        o.put("totalJoints", totalJoints)
        o.put("perfectJoints", perfectJoints)
        o.put("bestTA", bestTimeAttack)
        o.put("bestEndless", bestEndless)
        o.put("leakFreeMask", leakFreeMask)
        o.put("tutorialDone", tutorialDone)
        o.put("streak", dailyStreak)
        o.put("lastDay", lastPlayDay)
        o.put("stars", JSONArray().apply { stars.forEach { put(it) } })
        o.put("seenAch", JSONArray().apply { seenAchievements.forEach { put(it) } })
        prefs.edit().putString("data", o.toString()).apply()
    }
}
