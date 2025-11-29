package com.example.myuniplacementapp.ui.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.myuniplacementapp.data.local.PlacementEntity
import com.example.myuniplacementapp.ui.theme.Black
import com.example.myuniplacementapp.ui.theme.DarkGold
import com.example.myuniplacementapp.ui.theme.White
import com.example.myuniplacementapp.ui.theme.isAppInDarkTheme
import com.example.myuniplacementapp.viewmodel.ApplicationViewModel
import com.example.myuniplacementapp.viewmodel.PlacementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    userEmail: String,
    applicationViewModel: ApplicationViewModel,
    placementViewModel: PlacementViewModel,
    onOpenDetails: (String) -> Unit,
    onBack: () -> Unit
) {
    val placements by placementViewModel.placements.collectAsState()
    val applications by applicationViewModel.getUserApplications(userEmail)
        .collectAsState(initial = emptyList())

    var selectedLocation by remember { mutableStateOf("All") }
    var sortExpanded by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("DATE") }

    val locations = listOf("All", "DUBLIN", "LIMERICK", "BELFAST", "ERASMUS")

    val joined = applications.mapNotNull { app ->
        val placement = placements.find { it.id == app.placementId }
        placement?.let { Triple(app, it, it.location.name) }
    }

    val filtered =
        if (selectedLocation == "All") joined
        else joined.filter { it.third.equals(selectedLocation, ignoreCase = true) }

    val sorted = when (sortOption) {
        "DATE" -> filtered.sortedByDescending { it.first.appliedDate }
        "ALPHA" -> filtered.sortedBy { it.second.company }
        "STATUS" -> filtered.sortedBy { it.first.status.name }
        else -> filtered
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Applications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { pad ->

        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(DarkGold)
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                val isDark = isAppInDarkTheme()
                locations.forEach { loc ->
                    val isSelected = selectedLocation == loc
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLocation = loc },
                        label = { Text(loc) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) White else Black,
                            selectedLabelColor = if (isDark) Black else White,
                            labelColor = if (isDark) White else Black,
                            containerColor = Color.Transparent
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isDark) White else Black,
                            borderWidth = 1.5.dp,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(DarkGold)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sort by", Modifier.weight(1f))
                Box {
                    IconButton(onClick = { sortExpanded = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, null)
                    }
                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Applied date") },
                            onClick = {
                                sortOption = "DATE"
                                sortExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Company A–Z") },
                            onClick = {
                                sortOption = "ALPHA"
                                sortExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Status") },
                            onClick = {
                                sortOption = "STATUS"
                                sortExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(sorted) { (app, placement, _) ->
                    ApplicationCard(
                        placement = placement,
                        status = app.status.name,
                        onClick = { onOpenDetails(app.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(
    placement: PlacementEntity,
    status: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGold)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        SubcomposeAsyncImage(
            model = placement.companyLogo,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(12.dp))
        Text(placement.company, style = MaterialTheme.typography.titleMedium)
        Text(status, style = MaterialTheme.typography.bodySmall)
        Text(placement.location.name, style = MaterialTheme.typography.bodySmall)
    }
}