package com.weldrite.cpvcmaster.data

/** Snapshot of cumulative player stats used to evaluate achievements & ranks. */
data class Stats(
    val totalJoints: Int,
    val perfectJoints: Int,
    val leakFreeLevels: Int,
    val bestTimeAttack: Int,
    val bestEndless: Int,
    val threeStarLevels: Int,
    val completedLevels: Int,
    val totalStars: Int,
)

/**
 * Achievement catalogue. Each achievement is derived purely from cumulative
 * [Stats], so unlocking is consistent and replay-safe (no event can be missed).
 */
enum class Achievement(val id: String, val title: String, val desc: String) {
    FIRST_JOINT("first_joint", "First Joint", "Complete your very first connection."),
    LEAK_FREE("leak_free", "Leak-Free Expert", "Finish a level with zero leaks."),
    SPEED_PLUMBER("speed_plumber", "Speed Plumber", "Land 12+ joints in Time Attack."),
    PERFECT_100("perfect_100", "100 Perfect Connections", "Make 100 perfect joints."),
    TRIPLE_THREAT("triple_threat", "Triple Threat", "Earn 3 stars on 10 levels."),
    MARATHON("marathon", "Marathon", "Reach 20 joints in Endless Mode."),
    HALFWAY("halfway", "Journeyman", "Complete 25 career levels."),
    ALL_FIXED("all_fixed", "All Fixed Up", "Complete all 50 career levels."),
    MASTER_INSTALLER("master_installer", "Master Installer", "Reach Master Plumber rank.");

    fun isUnlocked(s: Stats): Boolean = when (this) {
        FIRST_JOINT -> s.totalJoints >= 1
        LEAK_FREE -> s.leakFreeLevels >= 1
        SPEED_PLUMBER -> s.bestTimeAttack >= 12
        PERFECT_100 -> s.perfectJoints >= 100
        TRIPLE_THREAT -> s.threeStarLevels >= 10
        MARATHON -> s.bestEndless >= 20
        HALFWAY -> s.completedLevels >= 25
        ALL_FIXED -> s.completedLevels >= 50
        MASTER_INSTALLER -> s.totalStars >= Rank.MASTER.starsNeeded
    }

    /** Progress fraction 0..1 toward unlocking, for display. */
    fun progress(s: Stats): Float = when (this) {
        FIRST_JOINT -> s.totalJoints / 1f
        LEAK_FREE -> s.leakFreeLevels / 1f
        SPEED_PLUMBER -> s.bestTimeAttack / 12f
        PERFECT_100 -> s.perfectJoints / 100f
        TRIPLE_THREAT -> s.threeStarLevels / 10f
        MARATHON -> s.bestEndless / 20f
        HALFWAY -> s.completedLevels / 25f
        ALL_FIXED -> s.completedLevels / 50f
        MASTER_INSTALLER -> s.totalStars.toFloat() / Rank.MASTER.starsNeeded
    }.coerceIn(0f, 1f)

    companion object {
        fun unlocked(s: Stats): List<Achievement> = entries.filter { it.isUnlocked(s) }
    }
}
