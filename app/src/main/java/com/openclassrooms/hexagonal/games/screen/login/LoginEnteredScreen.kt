package com.openclassrooms.hexagonal.games.screen.loginEntered

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.hexagonal.games.R
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.text.input.TextFieldValue
import com.openclassrooms.hexagonal.games.ui.theme.Purple40


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginEnteredScreen(
    onValidate: (String) -> Unit = {} // Callback pour valider l'email
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }

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

            Button(onClick = { onValidate(email.text) }) {
                Text(stringResource(R.string.Button_next))
            }
        }
    }
}

// 🎨 **Preview**
@Preview(showBackground = true)
@Composable
fun PreviewLoginEnteredScreen() {
    LoginEnteredScreen()
}
