package com.weldrite.cpvcmaster

import com.weldrite.cpvcmaster.data.Achievement
import com.weldrite.cpvcmaster.data.Environment
import com.weldrite.cpvcmaster.data.FittingType
import com.weldrite.cpvcmaster.data.Levels
import com.weldrite.cpvcmaster.data.PipeSize
import com.weldrite.cpvcmaster.data.Rank
import com.weldrite.cpvcmaster.data.Stats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Pure-JVM tests for the Android-free game logic. */
class LogicTest {

    @Test fun career_has_50_levels_with_progressive_difficulty() {
        val all = Levels.all
        assertEquals(50, all.size)
        // difficulty monotonically non-decreasing, 0..1
        var prev = -1f
        for (l in all) {
            assertTrue(l.difficulty in 0f..1f)
            assertTrue(l.difficulty >= prev - 1e-4f)
            prev = l.difficulty
            assertTrue(l.jointCount in 1..5)
            assertTrue(l.timeLimit > 0f)
            assertTrue(l.allowedPipes.isNotEmpty())
            assertTrue(l.fittingPool.isNotEmpty())
        }
    }

    @Test fun all_six_environments_are_represented() {
        val envs = Levels.all.map { it.environment }.toSet()
        assertEquals(Environment.entries.toSet(), envs)
    }

    @Test fun joint_options_always_contain_the_correct_answer() {
        val rng = Random(42)
        for (l in Levels.all) {
            repeat(20) {
                val j = l.makeJoint(rng)
                assertTrue("pipe target in options", j.pipe in j.pipeOptions)
                assertTrue("fitting target in options", j.fitting in j.fittingOptions)
                assertTrue(j.pipeOptions.size in 2..PipeSize.entries.size)
                assertTrue(j.fittingOptions.size in 2..FittingType.entries.size)
                assertTrue(j.pipe in l.allowedPipes)
                assertTrue(j.fitting in l.fittingPool)
            }
        }
    }

    @Test fun endless_and_timeattack_jointconfigs_are_valid() {
        val rng = Random(7)
        repeat(60) { i ->
            val e = Levels.endlessJoint(i, rng)
            assertTrue(e.pipe in e.pipeOptions)
            assertTrue(e.fitting in e.fittingOptions)
            val t = Levels.timeAttackJoint(rng)
            assertTrue(t.pipe in t.pipeOptions)
            assertTrue(t.fitting in t.fittingOptions)
        }
    }

    @Test fun rank_thresholds_are_ordered_and_resolve() {
        assertEquals(Rank.APPRENTICE, Rank.forStars(0))
        assertEquals(Rank.MASTER, Rank.forStars(999))
        var prev = -1
        for (r in Rank.entries) { assertTrue(r.starsNeeded > prev); prev = r.starsNeeded }
        assertEquals(Rank.JUNIOR, Rank.next(Rank.APPRENTICE))
        assertEquals(null, Rank.next(Rank.MASTER))
    }

    @Test fun achievements_unlock_at_expected_stats() {
        val zero = Stats(0, 0, 0, 0, 0, 0, 0, 0)
        assertTrue(Achievement.unlocked(zero).isEmpty())

        val first = zero.copy(totalJoints = 1)
        assertTrue(Achievement.FIRST_JOINT.isUnlocked(first))

        val hundred = zero.copy(perfectJoints = 100)
        assertTrue(Achievement.PERFECT_100.isUnlocked(hundred))

        val master = zero.copy(totalStars = Rank.MASTER.starsNeeded)
        assertTrue(Achievement.MASTER_INSTALLER.isUnlocked(master))

        // progress is clamped 0..1
        for (a in Achievement.entries) {
            assertTrue(a.progress(zero) in 0f..1f)
            assertTrue(a.progress(master.copy(totalJoints = 999, perfectJoints = 999, completedLevels = 999)) in 0f..1f)
        }
    }
}
