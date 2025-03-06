package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.data.service.firebase.MyFirebaseMessagingService
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.screen.login.FindPasswordScreen
import com.openclassrooms.hexagonal.games.screen.login.LoginEnteredScreen
import com.openclassrooms.hexagonal.games.screen.login.LoginPasswordScreen
import com.openclassrooms.hexagonal.games.screen.login.LoginScreen
import com.openclassrooms.hexagonal.games.screen.login.RegisterUserScreen
import com.openclassrooms.hexagonal.games.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.screen.userAccountManagement.UserAccountManagementScreen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity for the application. This activity serves as the entry point and container for the navigation
 * fragment. It handles setting up the toolbar, navigation controller, and action bar behavior.
 */
@AndroidEntryPoint
class MainActivity :
    ComponentActivity() {

    @Inject
    lateinit var myFirebaseMessagingService: MyFirebaseMessagingService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            HexagonalGamesTheme {
                HexagonalGamesNavHost(navHostController = navController)
            }
        }
        myFirebaseMessagingService.fireBaseMessaging.token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "Token récupéré : $token")
                } else {
                    Log.e("FCM", "Erreur lors de la récupération du token", task.exception)
                }
            }
    }
}


@Composable
fun HexagonalGamesNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Homefeed.route) {
            HomefeedScreen(
                onPostClick = {
                    //TODO
                },
                onSettingsClick = {
                    navHostController.navigate(Screen.Settings.route)
                },
                onFABClick = {
                    navHostController.navigate(Screen.AddPost.route)
                },
                onUserAccountClick = {
                    navHostController.navigate(Screen.UserAccount.route)
                }
            )
        }
        composable(route = Screen.AddPost.route) {
            AddScreen(
                onBackClick = { navHostController.navigateUp() },
                onSaveClick = { navHostController.navigateUp() }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navHostController.navigateUp() }
            )
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navHostController = navHostController)
        }
        composable(route = Screen.LoginEntered.route) {
            LoginEnteredScreen(navController = navHostController)
        }
        composable(route = Screen.LoginPassword.route) {
            LoginPasswordScreen(
                email = "",
                navController = navHostController
            )
        }
        composable(route = Screen.LoginPassword.route + "/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            LoginPasswordScreen(email = email, navController = navHostController)
        }

        composable(route = Screen.RegisterUser.route + "/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterUserScreen(navController = navHostController)
        }

        composable(route = Screen.FindPassword.route) {
            FindPasswordScreen(navController = navHostController)
        }
        composable(route = Screen.UserAccount.route) {
            UserAccountManagementScreen(
                navController = navHostController
            )
        }


    }
}
