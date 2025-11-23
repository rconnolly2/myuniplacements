package com.example.myuniplacementapp.ui.auth

import androidx.compose.runtime.*
import com.example.myuniplacementapp.ui.login.LoginScreen
import com.example.myuniplacementapp.ui.register.RegisterScreen
import com.example.myuniplacementapp.viewmodel.LoginViewModel
import com.example.myuniplacementapp.viewmodel.UserViewModel

enum class AuthScreen { LOGIN, REGISTER }

@Composable
fun AuthHost(
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
) {
    var currentScreen by remember { mutableStateOf(AuthScreen.LOGIN) }

    when (currentScreen) {
        AuthScreen.LOGIN -> LoginScreen(
            loginViewModel = loginViewModel,
            userViewModel = userViewModel,
            onClickGoToRegister = { currentScreen = AuthScreen.REGISTER }
        )

        AuthScreen.REGISTER -> RegisterScreen(
            loginViewModel = loginViewModel,
            userViewModel = userViewModel,
            onGoToLogin = { currentScreen = AuthScreen.LOGIN }
        )
    }
}