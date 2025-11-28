package com.example.myuniplacementapp.ui.application

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.myuniplacementapp.viewmodel.ApplicationViewModel
import com.example.myuniplacementapp.viewmodel.PlacementViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    placementId: String,
    userEmail: String,
    placementViewModel: PlacementViewModel,
    viewModel: ApplicationViewModel,
    onBack: () -> Unit,
    onApplicationSent: () -> Unit
) {
    val placements by placementViewModel.placements.collectAsState()
    val placement = placements.find { it.id == placementId }

    var coverLetter by remember { mutableStateOf("") }
    var screenshotUri by remember { mutableStateOf<Uri?>(null) }
    var appliedDate by remember { mutableStateOf<Long?>(null) }
    var sending by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val pickScreenshot = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { screenshotUri = it }

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { appliedDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Apply") },
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
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            ApplyHeader(placement)

            Spacer(Modifier.height(26.dp))

            ScreenshotBox(
                screenshotUri = screenshotUri,
                onPick = {
                    pickScreenshot.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(Modifier.height(18.dp))

            DateSelector(
                appliedDate = appliedDate,
                onClick = { showDatePicker = true }
            )

            Spacer(Modifier.height(18.dp))

            CoverLetterBox(
                value = coverLetter,
                onChange = { coverLetter = it }
            )

            Spacer(Modifier.height(28.dp))

            SubmitButton(
                sending = sending,
                onClick = {
                    if (screenshotUri != null && appliedDate != null && coverLetter.isNotBlank()) {
                        sending = true
                        scope.launch {
                            val bytes = ctx.contentResolver.openInputStream(screenshotUri!!)!!.readBytes()
                            viewModel.submitApplication(
                                placementId,
                                userEmail,
                                coverLetter,
                                bytes,
                                appliedDate!!
                            )
                            delay(600)
                            onApplicationSent()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ApplyHeader(placement: com.example.myuniplacementapp.data.local.PlacementEntity?) {
    if (placement == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        SubcomposeAsyncImage(
            model = placement.companyLogo,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                placement.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                placement.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ScreenshotBox(screenshotUri: Uri?, onPick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (screenshotUri == null) {
            Button(onClick = onPick) { Text("Upload Screenshot") }
        } else {
            SubcomposeAsyncImage(
                model = screenshotUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun DateSelector(appliedDate: Long?, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.CalendarMonth, null)
        Spacer(Modifier.width(8.dp))
        Text(
            appliedDate?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
            } ?: "Select Applied Date"
        )
    }
}

@Composable
fun CoverLetterBox(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Cover letter") },
        maxLines = 10,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun SubmitButton(sending: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        if (sending) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp
            )
        } else {
            Text("Submit Application", style = MaterialTheme.typography.titleMedium)
        }
    }
}