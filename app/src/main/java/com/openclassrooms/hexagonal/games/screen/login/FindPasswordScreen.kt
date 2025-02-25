package com.openclassrooms.hexagonal.games.screen.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.Purple40

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindPasswordScreen(
    navController: NavController,
    viewModel: LoginEnteredViewModel = hiltViewModel()
) {

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var isEmailError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }
    val errorEmptyMail = stringResource(R.string.error_empty_email)
    val errorInvalidMail = stringResource(R.string.error_invalid_email)
    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") //Email format
    val loginState by viewModel.loginState.collectAsState()

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
    ) {paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = String.format(stringResource(R.string.instruction_find_password)),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentSize()
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {},
                enabled = !isEmailError && loginState !is LoginState.Loading &&email.text.isNotEmpty()
            ) {
                Text(
                    stringResource(R.string.send)

                )
            }

        }
    }
}

@Preview
@Composable
fun FindPasswordScreenPreview() {
    val navHostController = rememberNavController()
    FindPasswordScreen(navController = navHostController)
}