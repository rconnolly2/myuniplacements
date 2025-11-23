package com.example.myuniplacementapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.myuniplacementapp.viewmodel.LoginViewModel
import com.example.myuniplacementapp.data.local.AppDatabase
import com.example.myuniplacementapp.data.prefs.UserPreferencesRepository
import com.example.myuniplacementapp.data.remote.AnnouncementRemoteDataSource
import com.example.myuniplacementapp.data.remote.PlacementRemoteDataSource
import com.example.myuniplacementapp.data.remote.UserRemoteDataSource
import com.example.myuniplacementapp.repository.AnnouncementRepository
import com.example.myuniplacementapp.repository.PlacementRepository
import com.example.myuniplacementapp.repository.UserRepository
import com.example.myuniplacementapp.ui.UserApp
import com.example.myuniplacementapp.ui.auth.AuthHost
import com.example.myuniplacementapp.ui.login.LoginScreen
import com.example.myuniplacementapp.ui.theme.UserAppTheme
import com.example.myuniplacementapp.viewmodel.LoginState
import com.example.myuniplacementapp.viewmodel.SettingsViewModel
import com.example.myuniplacementapp.viewmodel.SettingsViewModelFactory
import com.example.myuniplacementapp.viewmodel.UserViewModel
import com.example.myuniplacementapp.viewmodel.UserViewModelFactory
import com.example.myuniplacementapp.utils.NetworkUtils
import com.example.myuniplacementapp.viewmodel.AnnouncementViewModel
import com.example.myuniplacementapp.viewmodel.AnnouncementViewModelFactory
import com.example.myuniplacementapp.viewmodel.PlacementViewModel
import com.example.myuniplacementapp.viewmodel.PlacementViewModelFactory
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
        )
        .fallbackToDestructiveMigration(true)
        .build()

        val userRepository = UserRepository(
            dao = db.userDao(),
            remote = UserRemoteDataSource(),
            isOnline = { NetworkUtils.isOnline(this) }
        )
        val userFactory = UserViewModelFactory(userRepository)
        val userViewModel: UserViewModel by viewModels { userFactory }

        val placementRepository = PlacementRepository(
            dao = db.placementDao(),
            remote = PlacementRemoteDataSource(),
            isOnline = { NetworkUtils.isOnline(this) }
        )
        val placementFactory = PlacementViewModelFactory(placementRepository)
        val placementViewModel: PlacementViewModel by viewModels { placementFactory }

        val announcementRepository = AnnouncementRepository(
            dao = db.announcementDao(),
            remote = AnnouncementRemoteDataSource(),
            isOnline = { NetworkUtils.isOnline(this) }
        )
        val announcementFactory = AnnouncementViewModelFactory(announcementRepository)
        val announcementViewModel: AnnouncementViewModel by viewModels { announcementFactory }

        setContent {
            val isDark by settingsViewModel.isDarkTheme.collectAsState()
            val loginState by loginViewModel.loginState.collectAsState()

            UserAppTheme(darkTheme = isDark) {
                if (loginState is LoginState.Success) {
                    UserApp(
                        userViewModel = userViewModel,
                        settingsViewModel = settingsViewModel,
                        loginViewModel = loginViewModel,
                        placementViewModel = placementViewModel,
                        announcementViewModel = announcementViewModel,
                    )
                } else {
                    AuthHost(
                        loginViewModel = loginViewModel,
                        userViewModel = userViewModel,
                    )
                }
            }
        }
    }
}
