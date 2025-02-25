package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPasswordScreen(
    email: String,
    onLoginSuccess: () -> Unit, // Callback en cas de succès
    viewModel: LoginEnteredViewModel = hiltViewModel(),
    navController: NavController,

    ) {
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    var triggerSignIn by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tittle_login_screen)) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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

            TextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                placeholder = { Text(stringResource(R.string.password)) },

                )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screen.FindPassword.route) },
                enabled = loginState !is LoginState.Loading
            ) {
                Text(
                    text = stringResource(R.string.trouble_sign_in),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { triggerSignIn = true },
                enabled = loginState !is LoginState.Loading
            ) {
                Text(
                    text = stringResource(
                        R.string.signIn
                    )
                )
            }

            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator() // Afficher l'indicateur de chargement pendant la connexion
                }

                is LoginState.Success -> {
                    LaunchedEffect(Unit) {
                        Log.d("LoginScreen", "Connexion réussie, navigation vers Homefeed")
                    }
                }

                is LoginState.Error -> {
                    // Afficher un message d'erreur en cas de connexion échouée
                    Text(
                        text = stringResource((loginState as LoginState.Error).message),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }
            LaunchedEffect(triggerSignIn) {
                if (triggerSignIn) {
                    // Appel suspendu à signIn et obtenir le résultat
                    val result = viewModel.signIn(email, password)

                    // Vérification du résultat
                    result.onSuccess {
                        // Si le résultat est un succès, on peut naviguer
                        Log.d("LoginScreen", "Connexion réussie, navigation vers Homefeed")
                        navController.navigate(Screen.Homefeed.route)
                    }.onFailure { error ->
                        // Si une erreur se produit, affiche l'erreur
                        Log.e("LoginScreen", "Erreur lors de la connexion: ${error.message}")
                        // Optionnellement, tu peux ajouter un état d'erreur dans loginState si nécessaire
                    }

                    // Réinitialiser triggerSignIn après l'appel pour éviter plusieurs exécutions
                    triggerSignIn = false
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginPassWordScreenPreview() {
    val navHostController = rememberNavController()
    LoginPasswordScreen(
        email = "jocelyn.testing@gmail.com",
        onLoginSuccess = { },
        navController = navHostController
    )
}
