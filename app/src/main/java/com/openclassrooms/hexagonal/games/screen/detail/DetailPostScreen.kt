package com.openclassrooms.hexagonal.games.screen.detail

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.Purple40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPostScreen(
    navController: NavController,
    onFABClick: () -> Unit,
    postId: String?,
    viewModel: DetailPostViewModel = hiltViewModel()
) {

    val post by viewModel.post.collectAsState()

    LaunchedEffect(postId) {
        // Fetch post with Id
        if (postId != null) {
            viewModel.fetchPostById(postId)
        }
    }

    Scaffold(
        modifier = Modifier.background(Purple40),
        topBar = {
            TopAppBar(
                title = { Text(text = post?.title ?: stringResource(R.string.loading)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Purple40)
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onFABClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.description_button_add)
                )
            }
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val image = viewModel.postImages.collectAsState().value[post?.id]


            if (post != null) {
                DetailPost(
                    image = image,
                    author = post!!.author?.name ?: "Auteur inconnu",
                    title = post!!.title,
                    description = post!!.description ?: "Pas de description"
                )
            }
        }
    }
}


@Composable
fun DetailPost(
    image: Bitmap?,
    author: String,
    title: String,
    description: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Card(elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.padding(4.dp) ) {

            Text(
                text = "By: $author",
                fontStyle = FontStyle.Italic

                )

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = title,

                )

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = description,
            )

            Spacer(modifier = Modifier.height(8.dp))

            image?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(maxHeight = 350.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

}


@Preview
@Composable
fun PreviewDetailPostScreen() {
    val navHostController = rememberNavController()
    DetailPostScreen(navController = navHostController, onFABClick = {}, postId = "1")
}