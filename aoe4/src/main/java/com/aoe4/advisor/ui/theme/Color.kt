package com.aoe4.advisor.ui.theme

import androidx.compose.ui.graphics.Color
import com.aoe4.advisor.model.Difficulty
import com.aoe4.advisor.model.Tier

// Brand palette — gold/parchment on dark, evoking AoE4's UI.
val Gold = Color(0xFFD4AF37)
val GoldDim = Color(0xFFB8962E)
val Parchment = Color(0xFFF2E6CC)
val DarkBg = Color(0xFF13110C)
val DarkSurface = Color(0xFF1E1A13)
val DarkSurfaceVariant = Color(0xFF2A2318)
val OnDark = Color(0xFFEDE3D0)
val OnDarkMuted = Color(0xFFC8B89C)
val OutlineBrown = Color(0xFF5A4E38)

/** Tier-list colours (S strongest … D weakest). */
fun tierColor(tier: Tier): Color = when (tier) {
    Tier.S -> Color(0xFFE5484D)
    Tier.A -> Color(0xFFE8833A)
    Tier.B -> Color(0xFFE6B53C)
    Tier.C -> Color(0xFF49A078)
    Tier.D -> Color(0xFF8896A6)
}

fun difficultyColor(d: Difficulty): Color = when (d) {
    Difficulty.EASY -> Color(0xFF5BB97B)
    Difficulty.INTERMEDIATE -> Color(0xFFE6B53C)
    Difficulty.HARD -> Color(0xFFE5705B)
}
