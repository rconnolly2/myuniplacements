package com.example.myuniplacementapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myuniplacementapp.utils.AuthUtils.loginUser
import com.example.myuniplacementapp.utils.AuthUtils.logoutUser
import com.example.myuniplacementapp.utils.AuthUtils.registerUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        loginUser(
            email = email,
            password = password,
            onSuccess = {
                _loginState.value = LoginState.Success
            },
            onError = { errorMessage ->
                _loginState.value = LoginState.Error(errorMessage)
            }
        )
    }

    fun register(email: String, password: String) {
        _loginState.value = LoginState.Loading

        registerUser(
            email = email,
            password = password,
            onSuccess = {
                _loginState.value = LoginState.Success
            },
            onError = { errorMessage ->
                _loginState.value = LoginState.Error(errorMessage)
            }
        )
    }

    fun logout() {
        _loginState.value = LoginState.Idle
        logoutUser()
    }
}

