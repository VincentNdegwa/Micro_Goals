package com.example.microgoals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.microgoals.navigation.Screen
import com.example.microgoals.ui.goals.GoalListScreen
import com.example.microgoals.ui.goals.AddEditGoalScreen
import com.example.microgoals.ui.statistics.StatisticsScreen
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microgoals.data.local.GoalDatabase
import com.example.microgoals.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseApp
import com.example.microgoals.ui.auth.AuthLoginScreen
import com.example.microgoals.ui.settings.SettingsScreen
import com.example.microgoals.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.microgoals.data.model.User

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val context = LocalContext.current
            val database = GoalDatabase.getDatabase(context)
            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModel.Factory(userDao = database.userDao())
            )

            val googleAuthClient = remember {
                GoogleAuthUiClient(
                    context = this,
                    oneTapClient = Identity.getSignInClient(this),
                    userViewModel = userViewModel
                )
            }
            val loggedInUser: User? = googleAuthClient.getSignedInUser()
            var isUserLoggedIn by remember { mutableStateOf(loggedInUser != null) }

            if (isUserLoggedIn && loggedInUser!=null) {
                val coroutineScope = rememberCoroutineScope()
                userViewModel.insertUser(loggedInUser)
                MainScreen(
                    googleAuthClient = googleAuthClient,
                    onLogout = {
                        coroutineScope.launch {
                            googleAuthClient.signOut()
                        }
                        isUserLoggedIn = false
                    }
                )
            } else {
                AuthLoginScreen(onLoginSuccess = {
                    isUserLoggedIn = true
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(googleAuthClient: GoogleAuthUiClient, onLogout: ()-> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Micro Goals") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == Screen.GoalList.route,
                    onClick = { navController.navigate(Screen.GoalList.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Goals") },
                    label = { Text("Goals") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == Screen.Statistics.route,
                    onClick = { navController.navigate(Screen.Statistics.route) },
                    icon = { Icon(painterResource(R.drawable.baseline_show_chart_24), contentDescription = "Statistics") },
                    label = { Text("Statistics") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "settings",
                    onClick = { navController.navigate("settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.GoalList.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.GoalList.route) {
                GoalListScreen(
                    onAddGoal = { navController.navigate(Screen.AddEditGoal.createRoute()) },
                    onEditGoal = { goalId ->
                        navController.navigate(Screen.AddEditGoal.createRoute(goalId))
                    }
                )
            }
            composable(
                route = Screen.AddEditGoal.route,
                arguments = listOf(navArgument("goalId") { type = NavType.LongType })
            ) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getLong("goalId") ?: -1L
                AddEditGoalScreen(
                    goalId = goalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    googleAuthClient = googleAuthClient,
                    onLogout = {

                        onLogout()
                    }
                )
            }
        }
    }
}