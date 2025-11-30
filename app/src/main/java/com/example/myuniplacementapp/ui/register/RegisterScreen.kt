package com.example.myuniplacementapp.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myuniplacementapp.R
import com.example.myuniplacementapp.data.local.UserEntity
import com.example.myuniplacementapp.ui.theme.Black
import com.example.myuniplacementapp.ui.theme.White
import com.example.myuniplacementapp.ui.theme.isAppInDarkTheme
import com.example.myuniplacementapp.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    loginViewModel: LoginViewModel = viewModel(),
    userViewModel: UserViewModel,
    onGoToLogin: () -> Unit
) {
    val state by loginViewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            onGoToLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Register", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.register_students),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f))
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "If you already have an account please go to login.",
                            color = MaterialTheme.colorScheme.onSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(20.dp))

                        val isDark = isAppInDarkTheme()
                        Button(
                            onClick = onGoToLogin,
                            shape = RoundedCornerShape(10.dp),
                            colors = if (isDark) {
                                ButtonDefaults.buttonColors(
                                    containerColor = Black,
                                    contentColor = White
                                )
                            } else {
                                ButtonDefaults.buttonColors()
                            }
                        ) { Text("Click here") }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            Column(Modifier.padding(horizontal = 24.dp)) {

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Your TUS email"
                )

                Spacer(Modifier.height(16.dp))

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Your password",
                    isPassword = true
                )

                Spacer(Modifier.height(16.dp))

                AuthTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "Your first name"
                )

                Spacer(Modifier.height(16.dp))

                AuthTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Your last name"
                )

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (password.isNotBlank() && email.isNotBlank() &&
                            firstName.isNotBlank() && lastName.isNotBlank()
                        ) {
                            loginViewModel.register(email, password)
                            userViewModel.addUser(
                                UserEntity(email, firstName, lastName, "", null, null, null)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Register", style = MaterialTheme.typography.titleLarge)
                }

                when (state) {
                    is LoginState.Loading -> {
                        Spacer(Modifier.height(20.dp))
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is LoginState.Error -> {
                        Spacer(Modifier.height(20.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (state as LoginState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Close, null)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

