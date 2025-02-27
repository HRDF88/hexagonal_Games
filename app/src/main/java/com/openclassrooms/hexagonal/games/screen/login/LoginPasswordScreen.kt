package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.ui.theme.Purple40

/**
 * Composable function representing the screen where the user enters their password to log in.
 *
 * This screen allows the user to input their password and sign in. If the user encounters issues with signing in,
 * they can navigate to a screen to reset their password. The screen also handles loading and error states
 * and displays messages accordingly.
 *
 * @param email The email of the user, passed from the previous screen.
 * @param viewModel The [LoginEnteredViewModel] responsible for managing login state and authentication logic.
 * @param navController The [NavController] used for managing navigation between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPasswordScreen(
    email: String,
    viewModel: LoginEnteredViewModel = hiltViewModel(),
    navController: NavController,

    ) {

    // State variables for managing password input and sign-in trigger
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    var triggerSignIn by remember { mutableStateOf(false) }

    // Scaffold to structure the screen with a top bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tittle_login_screen)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Purple40)
            )
        },
    ) { paddingValues ->
        // Main content of the screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Welcome message
            Text(
                text = String.format(
                    stringResource(R.string.welcome_back_message), email
                ),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input field
            TextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                placeholder = { Text(stringResource(R.string.password)) },

                )

            Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
            // Link to navigate to the password reset screen
            Button(
                onClick = { navController.navigate(Screen.FindPassword.route) },
                enabled = loginState !is LoginState.Loading
            ) {
                Text(
                    text = stringResource(R.string.trouble_sign_in),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold

                )
            }
        }
            Spacer(modifier = Modifier.height(16.dp))

            // Sign-In button
            Button(
                onClick = { triggerSignIn = true },
                enabled = loginState !is LoginState.Loading && password.isNotEmpty()
            ) {
                Text(
                    text = stringResource(
                        R.string.signIn
                    )
                )
            }

            // Handle different login states (loading, success, error)
            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator() // Show loading indicator during sign-in process
                }

                is LoginState.Success -> {
                    LaunchedEffect(Unit) {
                    }
                }

                is LoginState.Error -> {
                    // Show an error message if sign-in fails
                    Text(
                        text = stringResource((loginState as LoginState.Error).message),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }

            // Handle sign-in process asynchronously when triggered
            LaunchedEffect(triggerSignIn) {
                if (triggerSignIn) {
                    // Call the signIn function and check the result
                    val result = viewModel.signIn(email, password)

                    // Navigate to the home screen on success
                    result.onSuccess {
                        navController.navigate(Screen.Homefeed.route)
                    }.onFailure { error ->
                        Log.e("LoginScreen", "Erreur lors de la connexion: ${error.message}")

                    }
                    // Reset the trigger after the sign-in attempt to prevent multiple executions
                    triggerSignIn = false
                }
            }
        }
    }
}

/**
 * Preview of the LoginPasswordScreen composable for design-time visualization.
 */
@Composable
@Preview(showBackground = true)
fun LoginPassWordScreenPreview() {
    val navHostController = rememberNavController()
    LoginPasswordScreen(
        email = "jocelyn.testing@gmail.com",
        navController = navHostController
    )
}
