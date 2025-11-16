package com.example.userapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.userapp.data.local.UserDao
import com.example.userapp.repository.UserRepository
import com.example.userapp.viewmodel.LoginState
import com.example.userapp.viewmodel.LoginViewModel
import com.example.userapp.viewmodel.UserViewModel
import com.example.userapp.viewmodel.UserViewModelFactory

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by loginViewModel.loginState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        when (state) {
            is LoginState.Error -> Text(
                text = (state as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error
            )

            is LoginState.Loading -> CircularProgressIndicator()

            else -> {}
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { loginViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        TextButton(
            onClick = {
                loginViewModel.register(email, password)
                userViewModel.addUser("", "", email, "", "")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register New Account")
        }
    }
}
