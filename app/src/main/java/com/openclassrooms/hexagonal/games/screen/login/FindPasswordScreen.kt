package com.openclassrooms.hexagonal.games.screen.login

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
 * Composable function representing the Find Password screen where users can request a password reset.
 *
 * This screen contains a form where users can input their email address. It validates the email format and
 * handles the logic for initiating a password reset request. If the request is successful, a confirmation
 * dialog is shown, and the user is navigated back to the login screen.
 *
 * @param navController The [NavController] to manage navigation between screens.
 * @param viewModel The [LoginEnteredViewModel] responsible for managing the authentication logic.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindPasswordScreen(
    navController: NavController,
    viewModel: LoginEnteredViewModel = hiltViewModel()
) {

    // State variables to handle user input, errors, and UI updates
    var email by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }
    val errorEmptyMail = stringResource(R.string.error_empty_email)
    val errorInvalidMail = stringResource(R.string.error_invalid_email)
    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") //Email format
    val loginState by viewModel.loginState.collectAsState()
    var triggerFindPassword by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val successMessage = stringResource(R.string.password_reset_success, email)

    // Scaffold layout to provide structure and top bar
    Scaffold(modifier = Modifier.background(Purple40),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tittle_find_password)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Purple40)
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Instruction text for the user
            Text(
                text = String.format(stringResource(R.string.instruction_find_password)),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentSize()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email TextField input with validation
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    when {
                        email.isEmpty() -> {
                            isEmailError = true
                            emailErrorMessage = errorEmptyMail
                        }

                        !email.matches(emailPattern) -> {
                            isEmailError = true
                            emailErrorMessage = errorInvalidMail
                        }

                        else -> {
                            isEmailError = false
                            emailErrorMessage = ""
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.email)) },
                isError = isEmailError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.wrapContentSize()
            )
            // Error message for invalid email input
            if (isEmailError) {
                Text(
                    text = emailErrorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to trigger password reset
            Button(
                onClick = { triggerFindPassword = true },
                enabled = !isEmailError && loginState !is LoginState.Loading && email.isNotEmpty()
            ) {
                Text(
                    stringResource(R.string.send)

                )
            }

            // Loading or error state display based on loginState
            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                }

                is LoginState.Success -> {
                    LaunchedEffect(Unit) {
                    }
                }

                is LoginState.Error -> {
                    // Display error message
                    Text(
                        text = stringResource((loginState as LoginState.Error).message),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }
            // Handle password reset action when triggered
            LaunchedEffect(triggerFindPassword) {
                if (triggerFindPassword) {
                    // Appel suspendu à signIn et obtenir le résultat
                    val result = viewModel.resetPassword(email)

                    result.onSuccess {
                        // On success, show the confirmation dialog
                        showDialog = true
                    }.onFailure { error ->
                        Log.e("LoginScreen", "Erreur lors de la connexion: ${error.message}")

                    }
                    // Reset trigger after execution
                    triggerFindPassword = false
                }
            }


            // Success dialog after successful password reset
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.title_success_find_password)) },
                    text = { Text(successMessage) },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false
                            navController.navigate(Screen.LoginEntered.route) // Redirection après fermeture
                        }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                )
            }
        }
    }
}


/**
 * Preview of the FindPasswordScreen composable for design-time visualization.
 */
@Preview
@Composable
fun FindPasswordScreenPreview() {
    val navHostController = rememberNavController()
    FindPasswordScreen(navController = navHostController)
}