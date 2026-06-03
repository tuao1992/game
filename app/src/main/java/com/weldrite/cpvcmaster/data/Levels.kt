package com.weldrite.cpvcmaster.data

import kotlin.math.roundToInt
import kotlin.random.Random

/** Full spec for a single pipe-joining task (one trip through the 6-step loop). */
data class JointConfig(
    val pipe: PipeSize,
    val pipeOptions: List<PipeSize>,
    val fitting: FittingType,
    val fittingOptions: List<FittingType>,
    val difficulty: Float,
)

/** A career level. Endless / Time Attack synthesize their own configs from difficulty. */
data class Level(
    val index: Int,
    val name: String,
    val environment: Environment,
    val jointCount: Int,
    val timeLimit: Float,
    val allowedPipes: List<PipeSize>,
    val fittingPool: List<FittingType>,
    val difficulty: Float,
) {
    val number: Int get() = index + 1

    fun makeJoint(rng: Random): JointConfig =
        Levels.makeJoint(allowedPipes, fittingPool, difficulty, rng)
}

object Levels {

    /** Career environment bands (counts sum to 50). */
    private val bands = listOf(
        Environment.KITCHEN to listOf(
            "Sink Supply", "Faucet Line", "Dishwasher Feed", "Under-Sink Repair",
            "Hot Water Run", "Filter Hookup", "Drain Riser", "Island Branch"
        ),
        Environment.BATHROOM to listOf(
            "Shower Riser", "Vanity Line", "Toilet Supply", "Tub Filler",
            "Towel-Warmer Feed", "Bidet Branch", "Mixing Valve", "Wet Wall Rough-In", "Heater Tie-In"
        ),
        Environment.HOUSE to listOf(
            "Main Trunk", "Cold Manifold", "Hot Manifold", "Garage Bib", "Laundry Box",
            "Water Heater", "Pressure Loop", "Attic Run", "Crawlspace Repair", "Hose Bibb",
            "Whole-House Filter"
        ),
        Environment.APARTMENT to listOf(
            "Unit 1A Riser", "Stack Branch", "Corridor Main", "Booster Line", "Roof Tank Feed",
            "Unit Manifold", "Shared Wall", "Balcony Bib", "Recirc Loop"
        ),
        Environment.COMMERCIAL to listOf(
            "Lobby Riser", "Restroom Bank", "Kitchen Hood Feed", "Sprinkler Branch",
            "Mechanical Room", "Roof Drain Tie", "Chiller Line", "Booster Pump"
        ),
        Environment.FACTORY to listOf(
            "Process Header", "Coolant Main", "Wash-Down Line", "Boiler Feed", "Plant Trunk"
        ),
    )

    val all: List<Level> by lazy { build() }

    private fun build(): List<Level> {
        val out = ArrayList<Level>(50)
        var index = 0
        val total = bands.sumOf { it.second.size } // 50
        for ((env, names) in bands) {
            for (name in names) {
                val diff = index.toFloat() / (total - 1)        // 0..1
                val jointCount = (1 + index * 4f / (total - 1)).roundToInt().coerceIn(1, 5)
                val perJoint = lerp(40f, 22f, diff)
                val timeLimit = perJoint * jointCount
                val maxPipe = (diff * (PipeSize.entries.lastIndex)).roundToInt().coerceIn(0, PipeSize.entries.lastIndex)
                val allowed = PipeSize.entries.subList(0, maxPipe + 1).toList()
                val fittingPool = when {
                    diff < 0.25f -> listOf(FittingType.ELBOW, FittingType.COUPLER)
                    diff < 0.55f -> listOf(FittingType.ELBOW, FittingType.COUPLER, FittingType.TEE)
                    else -> FittingType.entries.toList()
                }
                out += Level(
                    index = index,
                    name = "${env.displayName.substringBefore(' ')} • $name",
                    environment = env,
                    jointCount = jointCount,
                    timeLimit = timeLimit,
                    allowedPipes = allowed,
                    fittingPool = fittingPool,
                    difficulty = diff,
                )
                index++
            }
        }
        return out
    }

    /** Builds one joint config: picks a target pipe/fitting and a set of decoy options. */
    fun makeJoint(
        allowedPipes: List<PipeSize>,
        fittingPool: List<FittingType>,
        difficulty: Float,
        rng: Random,
    ): JointConfig {
        val pipe = allowedPipes[rng.nextInt(allowedPipes.size)]
        // Pipe selection options: include the target + neighbours, count scales with difficulty.
        val optCount = (2 + (difficulty * 3)).roundToInt().coerceIn(2, 5)
        val pipeOptions = buildOptions(PipeSize.entries.toList(), allowedPipes, pipe, optCount, rng)
            .sortedBy { it.ordinal }
        val fitting = fittingPool[rng.nextInt(fittingPool.size)]
        val fOptCount = (2 + (difficulty * 2)).roundToInt().coerceIn(2, fittingPool.size.coerceAtLeast(2))
        val fittingOptions = buildOptions(FittingType.entries.toList(), fittingPool, fitting, fOptCount, rng)
        return JointConfig(pipe, pipeOptions, fitting, fittingOptions, difficulty)
    }

    private fun <T> buildOptions(
        universe: List<T>, pool: List<T>, target: T, count: Int, rng: Random,
    ): List<T> {
        val set = linkedSetOf(target)
        val candidates = (pool + universe).distinct().filter { it != target }.shuffled(rng)
        for (c in candidates) {
            if (set.size >= count) break
            set += c
        }
        return set.toList().shuffled(rng)
    }

    /** Escalating difficulty for Endless: ramps up with joint index, then plateaus. */
    fun endlessJoint(jointIndex: Int, rng: Random): JointConfig {
        val diff = (jointIndex / 18f).coerceIn(0f, 1f)
        val maxPipe = (diff * PipeSize.entries.lastIndex).roundToInt().coerceIn(0, PipeSize.entries.lastIndex)
        val allowed = PipeSize.entries.subList(0, maxPipe + 1).toList()
        val pool = when {
            diff < 0.3f -> listOf(FittingType.ELBOW, FittingType.COUPLER)
            diff < 0.6f -> listOf(FittingType.ELBOW, FittingType.COUPLER, FittingType.TEE)
            else -> FittingType.entries.toList()
        }
        return makeJoint(allowed, pool, diff, rng)
    }

    /** Time Attack uses a steady moderate difficulty so the player can find a rhythm. */
    fun timeAttackJoint(rng: Random): JointConfig {
        val diff = 0.45f
        val allowed = PipeSize.entries.subList(0, 4).toList()
        val pool = listOf(FittingType.ELBOW, FittingType.COUPLER, FittingType.TEE)
        return makeJoint(allowed, pool, diff, rng)
    }

    private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
}
