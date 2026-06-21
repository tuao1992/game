package com.aoe4.advisor.model

/**
 * Domain model for the AoE4 Advisor app.
 *
 * The whole dataset is plain immutable Kotlin (see [com.aoe4.advisor.data.CivData]),
 * so there is no parsing/serialization at runtime — it is type-checked at compile time.
 */

/** Competitive tier (S strongest … D weakest). Derived from June 2026 Conqueror win rates. */
enum class Tier(val label: String) {
    S("S"), A("A"), B("B"), C("C"), D("D")
}

enum class Difficulty(val label: String) {
    EASY("Easy"), INTERMEDIATE("Intermediate"), HARD("Hard")
}

/** The three ages where a civilization commits to a Landmark / age-up choice. */
enum class Age(val label: String, val shortLabel: String) {
    FEUDAL("Feudal Age (II)", "Feudal"),
    CASTLE("Castle Age (III)", "Castle"),
    IMPERIAL("Imperial Age (IV)", "Imperial")
}

enum class UnitRole(val label: String) {
    INFANTRY("Infantry"),
    RANGED("Ranged"),
    CAVALRY("Cavalry"),
    GUNPOWDER("Gunpowder"),
    SIEGE("Siege"),
    RELIGIOUS("Religious"),
    NAVAL("Naval"),
    HERO("Hero"),
    ECONOMY("Economy")
}

/** Which release added the civilization (used for grouping + an "expansion" filter). */
enum class Dlc(val label: String, val year: Int) {
    BASE("Base game", 2021),
    FREE_EXPANSION("Free expansion", 2022),
    SULTANS_ASCEND("The Sultans Ascend", 2023),
    KNIGHTS_CROSS_ROSE("Knights of Cross and Rose", 2025),
    DYNASTIES_OF_THE_EAST("Dynasties of the East", 2025),
    YUE_FEI("Yue Fei's Legacy", 2026)
}

/** A recommended Landmark (or age-up) choice for a given age, plus the rationale. */
data class LandmarkPick(
    val age: Age,
    val best: String,
    val alternative: String?,
    val why: String
)

data class ArmyUnit(
    val name: String,
    val role: UnitRole,
    /** True if this unit is unique/signature to the civ (not a generic shared unit). */
    val unique: Boolean,
    /** True if this unit is part of the "best units" highlight. */
    val recommended: Boolean,
    val note: String
)

data class BuildOrder(
    val opening: String,
    val winCondition: String
)

data class Civ(
    val id: String,
    val name: String,
    /** Parent civ name when this is a variant; null for base/standalone civs. */
    val variantOf: String?,
    val dlc: Dlc,
    val tier: Tier,
    val difficulty: Difficulty,
    val beginnerFriendly: Boolean,
    /** ARGB accent colour used to theme the civ's cards/detail screen. */
    val accent: Long,
    val tagline: String,
    val overview: String,
    /** Solo-ranked win rate at Conqueror, AoE4 patch 16.2 (June 2026). Null if unknown. */
    val winRateConqueror: Double?,
    val pickRateConqueror: Double?,
    /** Explains a non-standard landmark/age-up system (House of Wisdom, single landmark, etc.). */
    val agingMechanic: String?,
    val bonuses: List<String>,
    val landmarks: List<LandmarkPick>,
    val units: List<ArmyUnit>,
    val coreArmy: String,
    val build: BuildOrder
) {
    val recommendedUnits: List<ArmyUnit> get() = units.filter { it.recommended }
    val uniqueUnits: List<ArmyUnit> get() = units.filter { it.unique }
    val isVariant: Boolean get() = variantOf != null
}
