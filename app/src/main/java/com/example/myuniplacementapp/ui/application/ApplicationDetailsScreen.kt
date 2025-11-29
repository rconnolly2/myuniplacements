@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myuniplacementapp.ui.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.myuniplacementapp.data.model.ApplicationStatus
import com.example.myuniplacementapp.data.model.pretty
import com.example.myuniplacementapp.viewmodel.ApplicationViewModel
import com.example.myuniplacementapp.viewmodel.PlacementViewModel
import com.example.myuniplacementapp.ui.theme.Gold
import kotlinx.coroutines.launch

@Composable
fun ApplicationDetailsScreen(
    applicationId: String,
    applicationViewModel: ApplicationViewModel,
    placementViewModel: PlacementViewModel,
    onBack: () -> Unit
) {
    val application by applicationViewModel.getApplicationById(applicationId)
        .collectAsState(initial = null)

    val placements by placementViewModel.placements.collectAsState()
    val placement = placements.find { it.id == application?.placementId }

    if (application == null || placement == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    var status by remember { mutableStateOf(application!!.status) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Application Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        applicationViewModel.updateStatus(applicationId, status)
                        snackbarHostState.showSnackbar("Status updated")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                )
            ) {
                Text("Save Changes", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                SubcomposeAsyncImage(
                    model = placement.companyLogo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(16.dp))
                Text(placement.company, style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(Modifier.height(20.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StatusProgress(status)
            }

            Spacer(Modifier.height(20.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StatusSelector(
                    selected = status,
                    onChange = { status = it }
                )
            }

            Spacer(Modifier.height(20.dp))

            Text("Cover Letter", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(application!!.coverLetter, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(20.dp))

            Text("Screenshot", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            SubcomposeAsyncImage(
                model = application!!.screenshotUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
fun StatusSelector(
    selected: ApplicationStatus,
    onChange: (ApplicationStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(selected.pretty())
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ApplicationStatus.entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.pretty()) },
                    onClick = {
                        expanded = false
                        onChange(it)
                    }
                )
            }
        }
    }
}

@Composable
fun StatusProgress(current: ApplicationStatus) {
    val order = listOf(
        ApplicationStatus.APPLIED,
        ApplicationStatus.PRE_SCREEN,
        ApplicationStatus.INTERVIEW,
        ApplicationStatus.OFFER,
        ApplicationStatus.NO_RESPONSE,
        ApplicationStatus.REJECTED
    )

    val currentIndex = order.indexOf(current)

    Column {
        order.forEachIndexed { index, step ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (index <= currentIndex) Gold else Color.LightGray)
                )
                Spacer(Modifier.width(12.dp))
                Text(step.pretty(), style = MaterialTheme.typography.bodyLarge)
            }

            if (index != order.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(2.dp)
                        .height(28.dp)
                        .background(if (index < currentIndex) Gold else Color.LightGray)
                )
            }
        }
    }
}