package com.example.myuniplacementapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.myuniplacementapp.data.local.AnnouncementEntity
import com.example.myuniplacementapp.viewmodel.AnnouncementViewModel
import com.example.myuniplacementapp.viewmodel.PlacementViewModel

@Composable
fun HomeScreen(
    placementViewModel: PlacementViewModel,
    announcementViewModel: AnnouncementViewModel,
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
    ) { innerPad ->

        val announcements by announcementViewModel.announcements.collectAsState()
        val placements by placementViewModel.placements.collectAsState()
        var selectedAnnouncement by remember { mutableStateOf<AnnouncementEntity?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPad)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(10.dp))

            Text(
                "Home",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(announcements) { ann ->
                    AsyncImage(
                        model = ann.image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 300.dp, height = 170.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { selectedAnnouncement = ann },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Local Placements",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp)
                )
                Text(
                    "→",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp)
                )
            }

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(placements) { placement ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(16.dp)
                            .clickable {
                                navController.navigate("apply/${placement.id}")
                            }
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
                            Text(
                                placement.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            AnnouncementPopup(
                announcement = selectedAnnouncement,
                onDismiss = { selectedAnnouncement = null }
            )
        }
    }
}

@Composable
fun AnnouncementPopup(
    announcement: AnnouncementEntity?,
    onDismiss: () -> Unit
) {
    if (announcement != null) {

        val formattedDate = announcement.addedDate.format(
            java.time.format.DateTimeFormatter.ofPattern(
                "d MMMM yyyy",
                java.util.Locale.getDefault()
            )
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {},
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        announcement.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    AsyncImage(
                        model = announcement.image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = announcement.content,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        TextButton(onClick = onDismiss) {
                            Text("Close")
                        }
                    }
                }
            }
        )
    }
}