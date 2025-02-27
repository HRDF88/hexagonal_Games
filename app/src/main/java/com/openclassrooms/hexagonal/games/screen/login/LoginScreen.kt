package com.openclassrooms.hexagonal.games.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen

/**
 * Composable function representing the login screen of the app.
 *
 * This screen serves as the entry point for the user to either log in or navigate to other screens.
 * The screen displays the app logo and a login button. When the button is clicked, it navigates
 * the user to the [LoginEnteredScreen] where they can enter their email.
 *
 * @param navHostController The [NavHostController] used for managing navigation actions.
 */
@Composable
fun LoginScreen(navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centers content vertically
    ) {
        // App Logo displayed at the top
        Image(
            painter = painterResource(id = R.drawable.logo_hex),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp) // Adjust the size as needed
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Login button to navigate to LoginEnteredScreen
        Button(
            onClick ={navHostController.navigate(Screen.LoginEntered.route)},
        ) {
            Text(text = stringResource(R.string.button_login_connect))
        }
    }
}

/**
 * Preview of the LoginScreen composable for design-time visualization.
 */
@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()
    LoginScreen(navHostController = navController)
}