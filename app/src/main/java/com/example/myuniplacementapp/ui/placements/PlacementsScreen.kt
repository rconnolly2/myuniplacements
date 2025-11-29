package com.example.myuniplacementapp.ui.placements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myuniplacementapp.data.local.PlacementEntity
import com.example.myuniplacementapp.viewmodel.PlacementViewModel
import com.example.myuniplacementapp.data.model.pretty

@Composable
fun PlacementsScreen(
    placementViewModel: PlacementViewModel,
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val applied = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Boolean>("applied") == true

    LaunchedEffect(applied) {
        if (applied) {
            snackbarHostState.showSnackbar("Application submitted successfully")
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("applied")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { pad ->

        val placements by placementViewModel.placements.collectAsState()
        val groups = remember(placements) { placements.groupBy { it.location.pretty() } }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(10.dp))

            Text(
                "Placements",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Here you can search and apply for placements based on the location nearest to your home!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(Modifier.height(20.dp))

            groups.forEach { (location, list) ->
                LocationSection(
                    location = location,
                    placements = list,
                    onPlacementClick = { id ->
                        navController.navigate("apply/$id")
                    }
                )
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun LocationSection(
    location: String,
    placements: List<PlacementEntity>,
    onPlacementClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$location Placements",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp)
        )
        Text(
            "→",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp)
        )
    }

    Spacer(Modifier.height(12.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(placements) { placement ->
            PlacementCard(
                placement = placement,
                onClick = { onPlacementClick(placement.id) }
            )
        }
    }
}

@Composable
private fun PlacementCard(
    placement: PlacementEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(280.dp)
            .background(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = placement.companyLogo,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                placement.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                placement.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}