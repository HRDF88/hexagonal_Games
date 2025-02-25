package com.openclassrooms.hexagonal.games.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.ui.theme.Purple40
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserScreen(
    email: String,
    viewModel: LoginEnteredViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val loginState by viewModel.loginState.collectAsState()

    val context = LocalContext.current
    val errorMessage = if (loginState is LoginState.Error) {
        stringResource(id = (loginState as LoginState.Error).message)
    } else {
        ""
    }
    var tryRegister by remember { mutableStateOf(false) }


    LaunchedEffect(loginState) {
        if (loginState is LoginState.Error && errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var email by remember { mutableStateOf(TextFieldValue("")) }
        var isEmailError by remember { mutableStateOf(false) }
        var emailErrorMessage by remember { mutableStateOf("") }

        var name by remember { mutableStateOf(TextFieldValue("")) }
        var isNameError by remember { mutableStateOf(false) }

        var password by remember { mutableStateOf(TextFieldValue("")) }
        var isPasswordError by remember { mutableStateOf(false) }
        var passwordErrorMessage by remember { mutableStateOf("") }

        //String error Message
        val errorEmptyMail = stringResource(R.string.error_empty_email)
        val errorInvalidMail = stringResource(R.string.error_invalid_email)
        val errorEmptyName = stringResource(R.string.error_empty_name)
        val errorPasswordShort = stringResource(R.string.error_short_password)

        val createUserSucess = stringResource(R.string.create_user_success)


        //Field pattern
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") //Email format
        val passwordPattern = Regex("^(?=.*\\d).{6,}$") // 6 chars long + 1 Maj + 1 figure


        Scaffold(modifier = Modifier.background(Purple40),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.tittle_create_user)) },
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
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        when {
                            email.text.isEmpty() -> {
                                isEmailError = true
                                emailErrorMessage = errorEmptyMail
                            }

                            !email.text.matches(emailPattern) -> {
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
                    modifier = Modifier.fillMaxWidth()
                )
                if (isEmailError) {
                    Text(
                        text = emailErrorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }


                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isNameError = name.text.isEmpty()
                    },
                    placeholder = { Text(stringResource(R.string.name)) },
                    isError = isNameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isNameError) {
                    Text(
                        text = errorEmptyName,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        when {
                            password.text.isEmpty() -> {
                                isPasswordError = true
                                passwordErrorMessage = errorPasswordShort
                            }

                            !password.text.matches(passwordPattern) -> {
                                isPasswordError = true
                                passwordErrorMessage = errorPasswordShort
                            }

                            else -> {
                                isPasswordError = false
                                passwordErrorMessage = ""
                            }
                        }
                    },
                    placeholder = { Text(stringResource(R.string.passwordField)) },
                    isError = isPasswordError,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(), // Cache le mot de passe
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isPasswordError) {
                    Text(
                        text = passwordErrorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))



                Button(
                    onClick = { tryRegister = true },
                    enabled = !isEmailError && !isNameError && !isPasswordError &&
                            email.text.isNotEmpty() && name.text.isNotEmpty() && password.text.matches(
                        passwordPattern
                    )
                ) {
                    Text(stringResource(R.string.save))
                }
            }

            // Appel de la méthode createAccount et gestion des résultats
            LaunchedEffect(tryRegister) {
                if (tryRegister) {
                    val result = viewModel.createAccount(
                        email = email.text,
                        password = password.text,
                        fullName = name.text
                    )

                    result.onSuccess {
                        // Succès - Affichage du Toast et redirection vers la page d'accueil
                        Toast.makeText(context, createUserSucess, Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Homefeed.route) // Navigation vers la page d'accueil
                    }.onFailure { e ->
                        // Erreur - Affichage du message d'erreur
                        val errorMessageRegister = when (e) {
                            is IOException -> "Problème de connexion réseau 🚨 : ${e.message}"
                            else -> "Erreur Firebase ❌ : ${e.message}"
                        }
                        Toast.makeText(context, errorMessageRegister, Toast.LENGTH_SHORT).show()
                    }
                }
                tryRegister = false

            }

        }
    }
}


@Preview
@Composable
fun RegisterUserScreenPreview() {
    val navHostController = rememberNavController()
    RegisterUserScreen(email = "", navController = navHostController)
}
