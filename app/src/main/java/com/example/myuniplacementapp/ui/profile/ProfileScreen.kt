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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myuniplacementapp.data.local.UserEntity
import com.example.myuniplacementapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: UserViewModel, onBack: () -> Unit) {

    val context = LocalContext.current
    val email = FirebaseAuth.getInstance().currentUser?.email ?: return
    val user by viewModel.getUserFlow(email).collectAsState(initial = null)

    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<ByteArray?>(null) }

    var editing by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = null)

    LaunchedEffect(user) {
        user?.let {
            first = it.firstName
            last = it.lastName
            phone = it.phoneNumber
            dob = it.dateOfBirth?.toString() ?: ""
            image = it.profileImageBlob
        }
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                image = context.contentResolver.openInputStream(it)?.readBytes()
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
            bmp?.let {
                val stream = ByteArrayOutputStream()
                it.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
                image = stream.toByteArray()
            }
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, null)
                    }
                },
                actions = {
                    IconButton(onClick = { editing = !editing }) {
                        Icon(Icons.Default.Edit, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileImage(image) { galleryLauncher.launch("image/*") }

            Spacer(Modifier.height(24.dp))

            ProfileField("First Name", first, editing) { first = it }
            ProfileField("Last Name", last, editing) { last = it }
            ProfileField("Phone", phone, editing) { phone = it }

            DOBField(dob, editing) { showDatePicker = true }

            ProfileDatePickerDialog(
                showDatePicker,
                datePickerState,
                { showDatePicker = false },
                {
                    datePickerState.selectedDateMillis?.let { picked ->
                        dob = Instant.ofEpochMilli(picked)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }
            )

            if (editing) {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        viewModel.saveUser(
                            UserEntity(
                                email = email,
                                firstName = first,
                                lastName = last,
                                phoneNumber = phone,
                                dateOfBirth = if (dob.isNotBlank()) LocalDate.parse(dob) else null,
                                profileImageBlob = image
                            )
                        )
                        scope.launch {
                            snackbarHost.showSnackbar("Profile saved successfully")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Save Changes") }
            }
        }
    }
}

@Composable
fun ProfileImage(image: ByteArray?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (image == null) {
            Icon(Icons.Default.Person, null, Modifier.size(70.dp))
        } else {
            val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
            Image(bmp.asImageBitmap(), null)
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
fun DOBField(dob: String, editing: Boolean, onClick: () -> Unit) {

    val active = OutlinedTextFieldDefaults.colors(
        disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface
    )

    val inactive = OutlinedTextFieldDefaults.colors()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = editing) { onClick() }
    ) {
        OutlinedTextField(
            value = dob,
            onValueChange = {},
            label = { Text("Date of Birth") },
            readOnly = true,
            enabled = false,
            colors = if (editing) active else inactive,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDatePickerDialog(
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
        ) {
            DatePicker(state = state)
        }
    }
}