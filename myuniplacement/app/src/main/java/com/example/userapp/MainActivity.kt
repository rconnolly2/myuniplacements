package com.example.userapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.userapp.viewmodel.LoginViewModel
import com.example.userapp.data.local.AppDatabase
import com.example.userapp.data.prefs.UserPreferencesRepository
import com.example.userapp.data.remote.UserRemoteDataSource
import com.example.userapp.repository.UserRepository
import com.example.userapp.ui.UserApp
import com.example.userapp.ui.login.LoginScreen
import com.example.userapp.ui.theme.UserAppTheme
import com.example.userapp.viewmodel.LoginState
import com.example.userapp.viewmodel.SettingsViewModel
import com.example.userapp.viewmodel.SettingsViewModelFactory
import com.example.userapp.viewmodel.UserViewModel
import com.example.userapp.viewmodel.UserViewModelFactory
import com.example.userapp.utils.NetworkUtils
import kotlin.jvm.java

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPrefsRepo = UserPreferencesRepository(dataStore)
        val settingsFactory = SettingsViewModelFactory(userPrefsRepo)
        val settingsViewModel: SettingsViewModel by viewModels { settingsFactory }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_db"
        ).build()

        val userRepository = UserRepository(
            dao = db.userDao(),
            remote = UserRemoteDataSource(),
            isOnline = { NetworkUtils.isOnline(this) }
        )
        val userFactory = UserViewModelFactory(userRepository)
        val userViewModel: UserViewModel by viewModels { userFactory }

        setContent {
            val isDark by settingsViewModel.isDarkTheme.collectAsState()
            val loginState by loginViewModel.loginState.collectAsState()

            UserAppTheme(darkTheme = isDark) {
                if (loginState is LoginState.Success) {
                    UserApp(
                        userViewModel = userViewModel,
                        settingsViewModel = settingsViewModel,
                        loginViewModel = loginViewModel
                    )
                } else {
                    LoginScreen(
                        loginViewModel = loginViewModel,
                        userViewModel = userViewModel,
                        onLoginSuccess = { recreate() }
                    )
                }
            }
        }
    }
}
