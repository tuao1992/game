package com.aoe4.advisor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aoe4.advisor.model.Tier
import com.aoe4.advisor.ui.theme.tierColor

/** Square, colour-coded tier letter (S/A/B/C/D). */
@Composable
fun TierBadge(tier: Tier, modifier: Modifier = Modifier, size: Int = 36) {
    val color = tierColor(tier)
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.5.dp, color, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tier.label,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = (size * 0.5).sp
        )
    }
}

/** Rounded, lightly-tinted label used for difficulty, role, DLC, etc. */
@Composable
fun Pill(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .border(1.dp, color.copy(alpha = 0.55f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        }
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** A small coloured dot — used to denote a unit's role colour, etc. */
@Composable
fun Dot(color: Color, size: Int = 8) {
    Box(
        Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
    )
}

/** Section heading with an icon + title, for the detail screen. */
@Composable
fun SectionHeader(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/** Colour used to tint a unit role label/dot. */
@Composable
fun roleColor(role: com.aoe4.advisor.model.UnitRole): Color = when (role) {
    com.aoe4.advisor.model.UnitRole.INFANTRY -> Color(0xFFC0894A)
    com.aoe4.advisor.model.UnitRole.RANGED -> Color(0xFF6FB36F)
    com.aoe4.advisor.model.UnitRole.CAVALRY -> Color(0xFF7E9CD8)
    com.aoe4.advisor.model.UnitRole.GUNPOWDER -> Color(0xFFD08A8A)
    com.aoe4.advisor.model.UnitRole.SIEGE -> Color(0xFFB0A06A)
    com.aoe4.advisor.model.UnitRole.RELIGIOUS -> Color(0xFFCBA0E0)
    com.aoe4.advisor.model.UnitRole.NAVAL -> Color(0xFF5FB0C0)
    com.aoe4.advisor.model.UnitRole.HERO -> Color(0xFFE6B53C)
    com.aoe4.advisor.model.UnitRole.ECONOMY -> Color(0xFF9AB07A)
}

val CardPadding = PaddingValues(16.dp)

/** A wrapping row of chips/pills that flows onto multiple lines as needed. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipFlowRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) { content() }
}
