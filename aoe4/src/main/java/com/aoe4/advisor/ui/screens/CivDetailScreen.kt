package com.aoe4.advisor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aoe4.advisor.data.CivRepository
import com.aoe4.advisor.model.ArmyUnit
import com.aoe4.advisor.model.Civ
import com.aoe4.advisor.model.LandmarkPick
import com.aoe4.advisor.ui.components.ChipFlowRow
import com.aoe4.advisor.ui.components.Dot
import com.aoe4.advisor.ui.components.Pill
import com.aoe4.advisor.ui.components.SectionHeader
import com.aoe4.advisor.ui.components.TierBadge
import com.aoe4.advisor.ui.components.roleColor
import com.aoe4.advisor.ui.theme.difficultyColor
import com.aoe4.advisor.ui.theme.tierColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivDetailScreen(civId: String, onBack: () -> Unit) {
    val civ = CivRepository.byId(civId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(civ?.name ?: "Unknown", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (civ == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Civilization not found.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { HeaderCard(civ) }

            item {
                SectionContainer {
                    SectionHeader("Overview", Icons.Filled.Info)
                    Text(civ.overview, style = MaterialTheme.typography.bodyMedium)
                }
            }

            item {
                SectionContainer {
                    SectionHeader("Signature bonuses", Icons.Filled.CheckCircle)
                    civ.bonuses.forEach { BulletLine(it) }
                }
            }

            item {
                SectionContainer {
                    SectionHeader("Best landmarks", Icons.Filled.Place)
                    if (civ.agingMechanic != null) {
                        NoteCallout(civ.agingMechanic)
                        Spacer(Modifier.height(10.dp))
                    }
                    civ.landmarks.forEach { LandmarkRow(it, Color(civ.accent)) }
                }
            }

            item {
                SectionContainer {
                    SectionHeader("Best units", Icons.Filled.Star)
                    civ.recommendedUnits.forEach { UnitRow(it, highlight = true) }
                    val others = civ.units.filter { !it.recommended }
                    if (others.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Other notable units",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        others.forEach { UnitRow(it, highlight = false) }
                    }
                }
            }

            item {
                SectionContainer {
                    SectionHeader("Core army composition", Icons.AutoMirrored.Filled.List)
                    Text(civ.coreArmy, style = MaterialTheme.typography.bodyMedium)
                }
            }

            item {
                SectionContainer {
                    SectionHeader("Build order & win condition", Icons.Filled.Build)
                    LabeledBlock("Opening", civ.build.opening)
                    Spacer(Modifier.height(10.dp))
                    LabeledBlock("Win condition", civ.build.winCondition)
                }
            }

            item {
                Text(
                    "Meta data: ${CivRepository.PATCH}. Tier & win rates from aoe4world Conqueror " +
                        "stats; recommendations are competitive guidance, not absolute rules. " +
                        "Newer variant civs have lower-confidence tiers — see REPORT.md.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HeaderCard(civ: Civ) {
    val accent = Color(civ.accent)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(accent.copy(alpha = 0.55f), accent.copy(alpha = 0.12f))
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        civ.name.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Spacer(Modifier.size(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        civ.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        civ.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TierBadge(civ.tier, size = 44)
                    Text(
                        "${civ.tier.label}-tier",
                        style = MaterialTheme.typography.labelSmall,
                        color = tierColor(civ.tier),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        // Stats strip.
        ChipFlowRow(Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)) {
            Pill(civ.difficulty.label, color = difficultyColor(civ.difficulty))
            civ.variantOf?.let { Pill("Variant of $it", color = MaterialTheme.colorScheme.secondary) }
            Pill("${civ.dlc.label} · ${civ.dlc.year}", color = MaterialTheme.colorScheme.primary)
            civ.winRateConqueror?.let {
                Pill("Win ${"%.1f".format(it)}%", color = winRateColor(it))
            }
            civ.pickRateConqueror?.let {
                Pill("Pick ${"%.1f".format(it)}%", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun winRateColor(wr: Double): Color = when {
    wr >= 51.0 -> Color(0xFF5BB97B)
    wr >= 49.0 -> Color(0xFFE6B53C)
    else -> Color(0xFFE5705B)
}

@Composable
private fun SectionContainer(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun BulletLine(text: String) {
    Row(Modifier.padding(vertical = 3.dp)) {
        Text("•  ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun NoteCallout(text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Icon(
            Icons.Filled.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LandmarkRow(lp: LandmarkPick, accent: Color) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accent.copy(alpha = 0.22f))
                    .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    lp.age.shortLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.size(8.dp))
            Text(
                lp.best,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(lp.why, style = MaterialTheme.typography.bodySmall)
        if (lp.alternative != null) {
            Text(
                "Alternative: ${lp.alternative}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UnitRow(unit: ArmyUnit, highlight: Boolean) {
    val rc = roleColor(unit.role)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(Modifier.padding(top = 6.dp)) { Dot(rc, size = 10) }
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (highlight) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Recommended",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                }
                Text(
                    unit.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(unit.note, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.size(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Pill(unit.role.label, color = rc)
            if (unit.unique) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "unique",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LabeledBlock(label: String, body: String) {
    Column {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}
