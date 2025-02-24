package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginEnteredScreen(
    navController: NavController,
    viewModel: LoginEnteredViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    val loginState by viewModel.loginState.collectAsState()

    Scaffold(modifier = Modifier.background(Purple40),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tittle_login_screen)) },
                modifier = Modifier.background(Purple40)

            )
        }
    ) { paddingValues ->
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

            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(stringResource(R.string.email)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.d("LoginScreen", "Checking if email exists: ${email.text}")
                    // Passe une fonction de callback pour traiter le résultat
                    viewModel.checkIfEmailExists(email.text) { emailExists ->
                        Log.d("LoginScreen", "Email exists: $emailExists")
                        if (emailExists) {
                            // Si l'email existe, naviguer vers LoginPasswordScreen
                            Log.d(
                                "LoginScreen",
                                "Navigating to LoginPasswordScreen with email: ${email.text}"
                            )
                            navController.navigate(Screen.LoginPassword.route + "/${email.text}")
                        } else {
                            // Si l'email n'existe pas, naviguer vers RegisterUserScreen
                            Log.d(
                                "LoginScreen",
                                "Navigating to RegisterUserScreen with email: ${email.text}"
                            )
                            navController.navigate(Screen.RegisterUser.route + "/${email.text}")
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.Button_next))

                when (loginState) {
                    is LoginState.Loading -> {
                        CircularProgressIndicator()
                        Log.d("LoginScreen", "Login is loading...")
                    }

                    is LoginState.Error -> {
                        val errorMessage =
                            stringResource(id = (loginState as LoginState.Error).message)
                        Log.d("LoginScreen", "Error occurred: $errorMessage")
                        Text(text = errorMessage, color = Color.Red)
                    }

                    else -> {}
                }
            }

        }
    }
}

    // 🎨 **Preview**
    @Preview(showBackground = true)
    @Composable
    fun PreviewLoginEnteredScreen() {
        val navHostController = rememberNavController()
        LoginEnteredScreen(navController = navHostController)
    }
