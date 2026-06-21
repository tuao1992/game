package com.aoe4.advisor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aoe4.advisor.data.CivRepository
import com.aoe4.advisor.model.Civ
import com.aoe4.advisor.model.Tier
import com.aoe4.advisor.ui.components.ChipFlowRow
import com.aoe4.advisor.ui.theme.tierColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TierListScreen(onCivClick: (String) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tier list", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "1v1 competitive standing · ${CivRepository.PATCH}. Derived from aoe4world " +
                        "Conqueror win rates; tiers for the newest variant civs are lower-confidence.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CivRepository.byTier().forEach { (tier, civs) ->
                item(key = tier.name) { TierRow(tier, civs, onCivClick) }
            }
        }
    }
}

@Composable
private fun TierRow(tier: Tier, civs: List<Civ>, onCivClick: (String) -> Unit) {
    val color = tierColor(tier)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Coloured tier header.
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(color.copy(alpha = 0.18f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.30f))
                        .border(1.5.dp, color, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tier.label, color = color, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "${tier.label}-tier",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "${civs.size} civ${if (civs.size == 1) "" else "s"}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            ChipFlowRow(Modifier.fillMaxWidth().padding(12.dp)) {
                civs.forEach { civ -> CivChip(civ, onCivClick) }
            }
        }
    }
}

@Composable
private fun CivChip(civ: Civ, onCivClick: (String) -> Unit) {
    val accent = Color(civ.accent)
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = 0.16f))
            .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(50))
            .clickable { onCivClick(civ.id) }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(accent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                civ.name.first().toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            civ.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
