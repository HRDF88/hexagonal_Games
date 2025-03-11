package com.openclassrooms.hexagonal.games.screen.adScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.Purple40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(
    navController: NavController,
    postId: String?,
    viewmodel: AddCommentViewmodel = hiltViewModel()

) {
    val uiState by viewmodel.uiState.collectAsState()
    val context = LocalContext.current
    val errorMessage = uiState.error?.let {
        stringResource(id = it)
    } ?: ""
    var comment by remember { mutableStateOf(TextFieldValue("")) }
    val maxChar = 150
    var isCommentError by remember { mutableStateOf(false) }
    var commentErrorMessage by remember { mutableStateOf("") }
    val errorEmptyComment = stringResource(R.string.error_empty_comment)


    var triggerAddComment by remember { mutableStateOf(false) }
    val addCommentSuccess = stringResource(R.string.add_comment_success)

    SideEffect {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(modifier = Modifier.background(Purple40),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tittle_add_comment)) },
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
        // Main content of the screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = comment,
                onValueChange = {
                    comment = it
                    if (it.text.length <= maxChar) {
                        comment = it
                        isCommentError = it.text.isEmpty()
                        commentErrorMessage = if (isCommentError) errorEmptyComment else ""
                    }
                },
                placeholder = { Text(stringResource(R.string.comment)) },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()

            )
            if (isCommentError) {
                Text(
                    text = commentErrorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }


            // Register Button
            Button(
                onClick = { triggerAddComment = true },
                enabled = !isCommentError && comment.text.isNotEmpty()

            ) {
                Text(stringResource(R.string.save))
            }


            if (uiState.loading) {
                CircularProgressIndicator()
            }
        }
    }

    LaunchedEffect(triggerAddComment) {
        if (triggerAddComment) {
            if (postId != null) {
                viewmodel.addComment(postId, comment.text)
                Toast.makeText(context, addCommentSuccess, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
        triggerAddComment = false
    }
}



@Preview
@Composable
fun PreviewAddCommentScreen() {
    val navHostController = rememberNavController()
    AddCommentScreen(navController = navHostController, postId = "1")
}
