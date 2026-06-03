package com.weldrite.cpvcmaster.data

/**
 * Core domain enums and value types for Weldrite CPVC Master.
 * Kept free of Android dependencies so they are trivially testable / reusable.
 */

/** CPVC pipe sizes the player can work with. [relDia] is a relative drawing diameter. */
enum class PipeSize(val label: String, val relDia: Float) {
    HALF("1/2\"", 0.74f),
    THREE_QUARTER("3/4\"", 0.92f),
    ONE("1\"", 1.12f),
    ONE_HALF("1.5\"", 1.46f),
    TWO("2\"", 1.78f);

    companion object {
        fun random(maxIndex: Int = entries.lastIndex) = entries[(0..maxIndex).random()]
    }
}

/** Fitting types used in the align/join steps. */
enum class FittingType(val label: String) {
    ELBOW("Elbow"),
    TEE("Tee"),
    COUPLER("Coupler"),
    REDUCER("Reducer");

    companion object {
        fun random() = entries.random()
    }
}

/** Visual/thematic environments for career progression. */
enum class Environment(val displayName: String, val accent: Int) {
    KITCHEN("Kitchen", 0xFF2E7D32.toInt()),
    BATHROOM("Bathroom", 0xFF0097A7.toInt()),
    HOUSE("House Plumbing", 0xFF1565C0.toInt()),
    APARTMENT("Apartment Project", 0xFF6A1B9A.toInt()),
    COMMERCIAL("Commercial Building", 0xFFEF6C00.toInt()),
    FACTORY("Factory Installation", 0xFF455A64.toInt());
}

enum class GameMode { CAREER, TIME_ATTACK, ENDLESS, TUTORIAL }

/** Player ranks, unlocked by accumulating stars. */
enum class Rank(val title: String, val starsNeeded: Int) {
    APPRENTICE("Apprentice Plumber", 0),
    JUNIOR("Junior Technician", 10),
    TECHNICIAN("Technician", 25),
    SENIOR("Senior Technician", 45),
    EXPERT("Plumbing Expert", 75),
    MASTER("Master Plumber", 110);

    companion object {
        fun forStars(stars: Int): Rank {
            var r = APPRENTICE
            for (rank in entries) if (stars >= rank.starsNeeded) r = rank
            return r
        }

        /** Returns the next rank after [current], or null if already at the top. */
        fun next(current: Rank): Rank? =
            entries.getOrNull(current.ordinal + 1)
    }
}

enum class Language(val code: String, val displayName: String) {
    EN("en", "English"),
    ES("es", "Español"),
    HI("hi", "हिन्दी");
}

enum class Quality(val displayName: String, val particleScale: Float, val antialias: Boolean) {
    LOW("Low", 0.4f, false),
    MEDIUM("Medium", 1.0f, true),
    HIGH("High", 1.8f, true);
}

/** Outcome of the pressure test. */
enum class JointResult { PERFECT, MINOR_LEAK, MAJOR_LEAK }
