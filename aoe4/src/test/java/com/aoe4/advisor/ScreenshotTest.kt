package com.aoe4.advisor

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.aoe4.advisor.ui.screens.CivDetailScreen
import com.aoe4.advisor.ui.screens.CivListScreen
import com.aoe4.advisor.ui.screens.TierListScreen
import com.aoe4.advisor.ui.theme.Aoe4AdvisorTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders the real Compose screens to PNG files on the JVM using Robolectric's
 * native graphics + Roborazzi — no emulator/KVM required.
 * Output: the aoe4/screenshots directory.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xhdpi")
class ScreenshotTest {

    @get:Rule
    val rule = createComposeRule()

    private val dir = "/home/user/game/aoe4/screenshots"

    @Test
    fun civList() {
        rule.setContent {
            Aoe4AdvisorTheme { CivListScreen(onCivClick = {}, onTierListClick = {}) }
        }
        rule.onRoot().captureRoboImage("$dir/01_civ_list.png")
    }

    @Test
    fun civDetailEnglish() {
        rule.setContent {
            Aoe4AdvisorTheme { CivDetailScreen(civId = "english", onBack = {}) }
        }
        rule.onRoot().captureRoboImage("$dir/02_detail_english.png")
    }

    @Test
    fun civDetailMongols() {
        rule.setContent {
            Aoe4AdvisorTheme { CivDetailScreen(civId = "mongols", onBack = {}) }
        }
        rule.onRoot().captureRoboImage("$dir/03_detail_mongols.png")
    }

    @Test
    fun tierList() {
        rule.setContent {
            Aoe4AdvisorTheme { TierListScreen(onCivClick = {}, onBack = {}) }
        }
        rule.onRoot().captureRoboImage("$dir/04_tier_list.png")
    }
}
