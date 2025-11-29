package com.example.myuniplacementapp.ui.profile

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myuniplacementapp.data.local.UserEntity
import com.example.myuniplacementapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: UserViewModel, onBack: () -> Unit) {

    val context = LocalContext.current
    val email = FirebaseAuth.getInstance().currentUser?.email ?: return
    val user by viewModel.getUserFlow(email).collectAsState(initial = null)

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var cvBytes by remember { mutableStateOf<ByteArray?>(null) }

    var editing by remember { mutableStateOf(false) }
    var showPicker by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState()

    LaunchedEffect(user) {
        user?.let {
            first = it.firstName
            last = it.lastName
            phone = it.phoneNumber
            dob = it.dateOfBirth?.toString() ?: ""
            imageBytes = null
            cvBytes = null
        }
    }

    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageBytes = uri?.let { context.contentResolver.openInputStream(it)?.readBytes() }
        }

    val pickCv =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            cvBytes = uri?.let { context.contentResolver.openInputStream(it)?.readBytes() }
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { editing = !editing }) {
                        Icon(Icons.Default.Edit, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->

        Column(
            Modifier
                .padding(pad)
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileImage(
                imageBytes = imageBytes,
                imageUrl = user?.profileImageUrl,
                enabled = editing
            ) { pickImage.launch("image/*") }

            Spacer(Modifier.height(24.dp))

            ProfileField("First Name", first, editing) { first = it }
            ProfileField("Last Name", last, editing) { last = it }
            ProfileField("Phone", phone, editing) { phone = it }

            DobField(dob, editing) { showPicker = true }

            DateDialog(
                show = showPicker,
                state = dateState,
                onDismiss = { showPicker = false }
            ) {
                dateState.selectedDateMillis?.let {
                    dob = Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toString()
                }
                showPicker = false
            }

            if (editing) {

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { pickCv.launch("application/pdf") }
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload CV")
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        val updated = UserEntity(
                            email = email,
                            firstName = first,
                            lastName = last,
                            phoneNumber = phone,
                            dateOfBirth = if (dob.isNotBlank()) LocalDate.parse(dob) else null,
                            profileImageUrl = user?.profileImageUrl,
                            cvFileUrl = user?.cvFileUrl
                        )

                        viewModel.saveUser(updated, imageBytes, cvBytes)
                        scope.launch { snackbar.showSnackbar("Profile saved") }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Save Changes") }
            }
        }
    }
}

@Composable
fun ProfileImage(imageBytes: ByteArray?, imageUrl: String?, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            imageBytes != null -> {
                val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Image(bmp.asImageBitmap(), null)
            }
            !imageUrl.isNullOrEmpty() -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> Icon(Icons.Default.Person, null, Modifier.size(70.dp))
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, enabled: Boolean, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )
}

@Composable
fun DobField(value: String, editing: Boolean, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text("Date of Birth") },
        readOnly = true,
        enabled = editing,
        trailingIcon = {
            IconButton(
                onClick = onClick,
                enabled = editing
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    show: Boolean,
    state: DatePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = onConfirm) { Text("OK") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }
}