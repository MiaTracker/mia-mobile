package com.nara.mia.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nara.mia.mobile.R
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.infrastructure.imageUrl
import com.nara.mia.mobile.models.IMediaDetails
import java.util.Vector

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Details(media: IMediaDetails?, navController: NavController, viewModel: IDetailsViewModel, specifics: @Composable () -> Unit) {
    val pullRefreshState = rememberPullToRefreshState()
    var menuExposed by remember { mutableStateOf(false) }
    var deleteDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = "") {
        pullRefreshState.startRefresh()
    }

    if(pullRefreshState.isRefreshing) {
        viewModel.refresh { pullRefreshState.endRefresh() }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = { Text(text = media?.title ?: "") },
                actions = {
                    IconButton(onClick = { menuExposed = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_more_vert_24),
                            contentDescription = ""
                        )
                    }
                    DropdownMenu(
                        expanded = menuExposed,
                        onDismissRequest = { menuExposed = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                deleteDialogOpen = true
                                menuExposed = false
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_delete_24),
                                    contentDescription = ""
                                )
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            if(media != null) {
                val titles = buildTagString(media.alternativeTitles) { it.title }
                val genres = buildTagString(media.genres) { it.name }
                val tags = buildTagString(media.tags) { it.name }

                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = media.backdropPath?.let { imageUrl(it) },
                        contentDescription = "Backdrop image",
                        Modifier.fillMaxWidth()
                    )
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = media.posterPath?.let { imageUrl(it) },
                            contentDescription = "Backdrop image",
                            Modifier
                                .width(150.dp)
                                .absoluteOffset(10.dp, (-10).dp)
                                .zIndex(1.1f)
                        )
                        Column(
                            Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = media.title,
                                    Modifier.absolutePadding(160.dp)
                                )
                            }
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(10.dp)
                            ) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.absolutePadding(160.dp)
                                ) {
                                    specifics()
                                }
                            }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier
                                        .absolutePadding(160.dp)
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                ) {
                                    if(titles.isNotEmpty())
                                        Text(text = "Titles: $titles", softWrap = true)
                                    if(genres.isNotEmpty())
                                        Text(text = "Genres: $genres", softWrap = true)
                                    if(tags.isNotEmpty())
                                        Text(text = "Tags: $tags", softWrap = true)
                                }
                            }
                        }
                    }
                    Text(text = media.overview ?: "", Modifier.padding(10.dp, 5.dp))
                }
            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
            )
        }
    }

    if(deleteDialogOpen) {
        AlertDialog(
            onDismissRequest = { deleteDialogOpen = false },
            title = { Text(text = "Warning") },
            text = { Text(text = "Do you really want to delete '${media?.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    deleteDialogOpen = false
                    viewModel.delete(navController)
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialogOpen = false }) {
                    Text(text = "No")
                }
            }
        )
    }
}

fun <T> buildTagString(vec: Vector<T>, f: (T) -> String): String {
    val builder = StringBuilder()
    vec.forEachIndexed { i, t ->
        if(i != 0) builder.append(", ")
        builder.append(f(t))
    }
    return builder.toString()
}