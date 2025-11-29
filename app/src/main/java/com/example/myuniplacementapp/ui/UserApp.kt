package com.example.myuniplacementapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myuniplacementapp.ui.application.ApplyScreen
import com.example.myuniplacementapp.ui.application.MyApplicationsScreen
import com.example.myuniplacementapp.ui.applications.ApplicationDetailsScreen
import com.example.myuniplacementapp.ui.components.DrawerContent
import kotlinx.coroutines.launch
import com.example.myuniplacementapp.viewmodel.UserViewModel
import com.example.myuniplacementapp.viewmodel.SettingsViewModel
import com.example.myuniplacementapp.ui.home.HomeScreen
import com.example.myuniplacementapp.ui.profile.ProfileScreen
import com.example.myuniplacementapp.ui.settings.SettingsScreen
import com.example.myuniplacementapp.viewmodel.AnnouncementViewModel
import com.example.myuniplacementapp.viewmodel.ApplicationViewModel
import com.example.myuniplacementapp.viewmodel.LoginViewModel
import com.example.myuniplacementapp.viewmodel.PlacementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserApp(
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    loginViewModel: LoginViewModel,
    placementViewModel: PlacementViewModel,
    announcementViewModel: AnnouncementViewModel,
    applicationViewModel: ApplicationViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDark by settingsViewModel.isDarkTheme.collectAsState()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                DrawerContent(
                    navController = navController,
                    viewModel = loginViewModel,
                    drawerState = drawerState,
                    scope = scope,
                    isDarkTheme = isDark,
                    onThemeToggle = { settingsViewModel.toggleTheme() }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                val route = currentRoute ?: ""

                if (route != "profile" && !route.startsWith("apply") && !route.startsWith("application_details") && route != "my_applications") {
                    CenterAlignedTopAppBar(
                        title = { Text("MyUniPlacements") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, null)
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        placementViewModel = placementViewModel,
                        announcementViewModel = announcementViewModel,
                        navController = navController
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        viewModel = userViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("my_applications") {
                    MyApplicationsScreen(
                        userEmail = "robertoconnolly100@gmail.com",
                        applicationViewModel = applicationViewModel,
                        placementViewModel = placementViewModel,
                        onOpenDetails = { id -> navController.navigate("application_details/$id") },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("settings") {
                    SettingsScreen(navController, settingsViewModel)
                }
                composable("apply/{placementId}") { backStackEntry ->
                    val placementId = backStackEntry.arguments?.getString("placementId") ?: ""
                    ApplyScreen(
                        placementId = placementId,
                        userEmail = userViewModel.user.value?.email ?: "",
                        placementViewModel = placementViewModel,
                        viewModel = applicationViewModel,
                        onBack = { navController.popBackStack() },
                        onApplicationSent = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("applied", true)

                            navController.popBackStack()
                        }
                    )
                }
                composable("application_details/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    ApplicationDetailsScreen(
                        applicationId = id,
                        applicationViewModel = applicationViewModel,
                        placementViewModel = placementViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}