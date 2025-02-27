package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.ui.theme.Purple40

/**
 * Composable function representing the screen where the user enters their email to either log in or register.
 *
 * This screen allows the user to input their email address. Upon submission, it checks if the email is already
 * registered and navigates the user to the appropriate next screen (login or registration) based on the result.
 * It also handles loading and error states, and displays a toast message if an error occurs.
 *
 * @param navController The [NavController] to manage navigation between screens.
 * @param viewModel The [LoginEnteredViewModel] responsible for managing the login and user verification logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginEnteredScreen(
    navController: NavController,
    viewModel: LoginEnteredViewModel = hiltViewModel()
) {
    // State variables to handle email input, loading, and error states
    var email by remember { mutableStateOf(TextFieldValue("")) }
    val loginState by viewModel.loginState.collectAsState()
    var tryCheckMail by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val errorMessage =
        (loginState as? LoginState.Error)?.message?.let { stringResource(id = it) } ?: ""


    // SideEffect to show toast message if an error occurs
    SideEffect {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // Scaffold to structure the screen with top bar
    Scaffold(modifier = Modifier.background(Purple40),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tittle_login_screen)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Purple40)

            )
        }
    ) { paddingValues ->
        // Main content of the screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Spacer(modifier = Modifier.height(8.dp))

            // Space and email TextField for user input
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(stringResource(R.string.email)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to trigger email validation and navigation
            Button(
                onClick = { tryCheckMail = true },
                enabled = loginState !is LoginState.Loading

            ) {
                Text(stringResource(R.string.Button_next))
            }
            // Show loading indicator if login is in progress
            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                    Log.d("LoginScreen", "Login is loading...")
                }

                is LoginState.Error -> {

                }


                else -> {}
            }

        }
        // Handle email validation and navigation based on email existence
        LaunchedEffect(tryCheckMail) {
            if (tryCheckMail) {
                try {
                    // Call the suspend function to check if the email exists
                    val emailExists = viewModel.checkIfEmailExists(email.text)

                    // Navigate based on whether the email exists
                    if (emailExists) {
                        // Navigate to LoginPasswordScreen if the email exists
                        navController.navigate(Screen.LoginPassword.route + "/${email.text}")
                    } else {
                        // Navigate to RegisterUserScreen if the email doesn't exist
                        navController.navigate(Screen.RegisterUser.route + "/${email.text}")
                    }
                } catch (_: Exception) {
                }

                tryCheckMail = false
            }
        }


    }
}

/**
 * Preview of the LoginEnteredScreen composable for design-time visualization.
 */
@Preview(showBackground = true)
@Composable
fun PreviewLoginEnteredScreen() {
    val navHostController = rememberNavController()
    LoginEnteredScreen(navController = navHostController)
}
