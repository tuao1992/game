package com.aoe4.advisor.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aoe4.advisor.data.CivRepository
import com.aoe4.advisor.model.Civ
import com.aoe4.advisor.model.Tier
import com.aoe4.advisor.ui.components.Pill
import com.aoe4.advisor.ui.components.TierBadge
import com.aoe4.advisor.ui.theme.difficultyColor

private sealed interface CivFilter {
    data object All : CivFilter
    data object Beginner : CivFilter
    data class ByTier(val tier: Tier) : CivFilter
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivListScreen(
    onCivClick: (String) -> Unit,
    onTierListClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf<CivFilter>(CivFilter.All) }

    val civs = remember(query, filter) {
        CivRepository.search(query).filter { civ ->
            when (val f = filter) {
                CivFilter.All -> true
                CivFilter.Beginner -> civ.beginnerFriendly
                is CivFilter.ByTier -> civ.tier == f.tier
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AoE4 Advisor", fontWeight = FontWeight.Bold)
                        Text(
                            "Best landmarks & units per civilization",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onTierListClick) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tier list")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search civ, unit, or playstyle…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )

            FilterRow(filter) { filter = it }

            if (civs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No civilizations match.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(civs, key = { it.id }) { civ ->
                        CivCard(civ) { onCivClick(civ.id) }
                    }
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${civs.size} of ${CivRepository.all.size} civilizations · ${CivRepository.PATCH}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(selected: CivFilter, onSelect: (CivFilter) -> Unit) {
    val options: List<Pair<String, CivFilter>> = buildList {
        add("All" to CivFilter.All)
        add("Beginner" to CivFilter.Beginner)
        Tier.entries.forEach { add("${it.label}-tier" to CivFilter.ByTier(it)) }
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { (label, value) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CivCard(civ: Civ, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent strip + monogram.
            Box(
                Modifier
                    .width(56.dp)
                    .height(84.dp)
                    .background(Color(civ.accent).copy(alpha = 0.30f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(civ.accent)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        civ.name.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    civ.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    civ.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Pill(civ.difficulty.label, color = difficultyColor(civ.difficulty))
                    if (civ.isVariant) {
                        Pill("Variant", color = MaterialTheme.colorScheme.secondary)
                    } else if (civ.beginnerFriendly) {
                        Pill("Beginner", color = difficultyColor(com.aoe4.advisor.model.Difficulty.EASY))
                    }
                }
            }

            TierBadge(civ.tier, modifier = Modifier.padding(end = 14.dp))
        }
    }
}
