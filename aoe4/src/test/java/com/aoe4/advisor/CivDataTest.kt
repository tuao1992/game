package com.aoe4.advisor

import com.aoe4.advisor.data.CivRepository
import com.aoe4.advisor.model.Age
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Validates the bundled civ dataset is complete and internally consistent. */
class CivDataTest {

    private val civs = CivRepository.all

    @Test
    fun hasAll23Civilizations() {
        assertEquals(23, civs.size)
    }

    @Test
    fun idsAreUnique() {
        assertEquals(civs.size, civs.map { it.id }.toSet().size)
    }

    @Test
    fun everyCivHasOneLandmarkPickPerAge() {
        civs.forEach { civ ->
            assertEquals("${civ.name} should have 3 landmark picks", 3, civ.landmarks.size)
            val ages = civ.landmarks.map { it.age }.toSet()
            assertTrue("${civ.name} is missing a landmark age", ages.containsAll(Age.entries.toList()))
        }
    }

    @Test
    fun everyCivHasRecommendedUnitsAndContent() {
        civs.forEach { civ ->
            assertTrue("${civ.name} has no recommended units", civ.recommendedUnits.isNotEmpty())
            assertTrue("${civ.name} has no bonuses", civ.bonuses.isNotEmpty())
            assertTrue("${civ.name} has blank tagline", civ.tagline.isNotBlank())
            assertTrue("${civ.name} has blank overview", civ.overview.isNotBlank())
            assertTrue("${civ.name} has blank core army", civ.coreArmy.isNotBlank())
            assertTrue("${civ.name} has blank opening", civ.build.opening.isNotBlank())
            assertTrue("${civ.name} has blank win condition", civ.build.winCondition.isNotBlank())
        }
    }

    @Test
    fun winRatesArePlausible() {
        civs.forEach { civ ->
            civ.winRateConqueror?.let {
                assertTrue("${civ.name} has implausible win rate $it", it in 30.0..70.0)
            }
        }
    }

    @Test
    fun variantsReferenceRealParentCivs() {
        val names = civs.map { it.name }.toSet()
        civs.filter { it.isVariant }.forEach { civ ->
            assertTrue(
                "${civ.name}'s parent '${civ.variantOf}' is not in the roster",
                civ.variantOf in names
            )
        }
    }

    @Test
    fun tierGroupingCoversEveryCiv() {
        val grouped = CivRepository.byTier().sumOf { it.second.size }
        assertEquals(civs.size, grouped)
    }

    @Test
    fun searchMatchesByUnitName() {
        assertTrue(CivRepository.search("Longbowman").any { it.id == "english" })
        assertTrue(CivRepository.search("mangudai").any { it.id == "mongols" })
        assertTrue(CivRepository.search("cataphract").any { it.id == "byzantines" })
    }
}
