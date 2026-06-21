package com.aoe4.advisor.data

import com.aoe4.advisor.model.Civ
import com.aoe4.advisor.model.Tier

/**
 * Single access point to the bundled civilization dataset.
 *
 * The data is static and offline (no network), curated from a June 2026 deep-research
 * pass over aoe4world.com, the official Age of Empires site, the Fandom wiki and
 * community tier lists. See [CivData].
 */
object CivRepository {

    /** All civilizations, alphabetical by name. */
    val all: List<Civ> = CivData.civs.sortedBy { it.name }

    /** Data freshness footer shown in the UI. */
    const val PATCH = "Patch 16.2 · Season 13 (Yue Fei's Legacy) · June 2026"

    fun byId(id: String): Civ? = all.firstOrNull { it.id == id }

    /** Civs grouped by tier in S→D order, each list alphabetised. */
    fun byTier(): List<Pair<Tier, List<Civ>>> =
        Tier.entries.map { tier -> tier to all.filter { it.tier == tier } }
            .filter { it.second.isNotEmpty() }

    /** Case-insensitive search over name, tagline, variant parent and unique unit names. */
    fun search(query: String): List<Civ> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return all
        return all.filter { civ ->
            civ.name.lowercase().contains(q) ||
                civ.tagline.lowercase().contains(q) ||
                (civ.variantOf?.lowercase()?.contains(q) == true) ||
                civ.units.any { it.name.lowercase().contains(q) }
        }
    }
}
