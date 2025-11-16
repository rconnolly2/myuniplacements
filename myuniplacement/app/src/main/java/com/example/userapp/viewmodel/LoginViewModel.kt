package com.example.userapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userapp.utils.AuthUtils.loginUser
import com.example.userapp.utils.AuthUtils.logoutUser
import com.example.userapp.utils.AuthUtils.registerUser
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

