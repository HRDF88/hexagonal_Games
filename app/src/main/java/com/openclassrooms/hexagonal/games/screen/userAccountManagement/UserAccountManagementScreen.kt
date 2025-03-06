package com.openclassrooms.hexagonal.games.screen.userAccountManagement

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
fun UserAccountManagementScreen(
    navController: NavController,
    viewModel: UserAccountManagementViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val errorMessage = uiState.error?.let {
        stringResource(id = it)
    } ?: ""

    var showDialog by remember { mutableStateOf(false) }

    SideEffect {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tittle_user_management_screen)) },
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
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route)
                },
                enabled = !uiState.loading
            ) { Text(stringResource(R.string.sign_out)) }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                enabled = !uiState.loading
            ) { Text(stringResource(R.string.delete_account)) }

            if (uiState.loading) {
                CircularProgressIndicator()
            }
        }
    }

    // Display the AlertDialog for account deletion confirmation
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },  // Close dialog if clicked outside
            title = { Text(stringResource(R.string.delete_account_confirmation)) },
            text = { Text(stringResource(R.string.are_you_sure_delete_account)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount()
                        showDialog = false
                        navController.navigate(Screen.Login.route)
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}


@Composable
@Preview(showBackground = true)
fun UserAccountManagementScreenPreview() {
    val navHostController = rememberNavController()
    UserAccountManagementScreen(
        navController = navHostController
    )
}