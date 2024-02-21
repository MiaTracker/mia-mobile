package com.nara.mia.mobile.ui.components

import android.webkit.URLUtil
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nara.mia.mobile.R
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.infrastructure.imageUrl
import com.nara.mia.mobile.models.IMediaDetails
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.Source
import java.text.DateFormat
import java.util.Vector

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun Details(media: IMediaDetails?, navController: NavController, viewModel: IDetailsViewModel, specifics: @Composable () -> Unit) {
    val pullRefreshState = rememberPullToRefreshState()
    var menuExposed by remember { mutableStateOf(false) }
    var deleteDialogOpen by remember { mutableStateOf(false) }
    var bottomSheetSource by remember { mutableStateOf<Source?>(null) }
    val sourceSheetState = rememberModalBottomSheetState()
    var bottomSheetLog by remember { mutableStateOf<Log?>(null) }
    val logSheetState = rememberModalBottomSheetState()
    val uriHandler = LocalUriHandler.current

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

                    if(media.sources.any()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(10.dp, 5.dp)
                        ) {
                            Text(text = "Sources", style = MaterialTheme.typography.titleMedium)
                            media.sources.forEach { s ->
                                Source(s, Modifier.combinedClickable(onLongClick = {
                                    bottomSheetSource = s
                                }) {
                                    if(URLUtil.isValidUrl(s.url))
                                        uriHandler.openUri(s.url)
                                })
                            }
                        }
                    }

                    if(media.logs.any()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(10.dp, 5.dp)
                        ) {
                            Text(text = "Logs", style = MaterialTheme.typography.titleMedium)
                            media.logs.forEach { l ->
                                Log(l, Modifier.combinedClickable(onLongClick = {
                                    bottomSheetLog = l
                                }) { })
                            }
                        }
                    }
                }
            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
            )

            if(bottomSheetSource != null) {
                ModalBottomSheet(
                    onDismissRequest = { bottomSheetSource = null },
                    sheetState = sourceSheetState
                ) {
                    Column {
                        if(URLUtil.isValidUrl(bottomSheetSource?.url)) {
                            ListItem(
                                headlineContent = { Text("Open link") },
                                leadingContent = {
                                    Icon(painter = painterResource(id = R.drawable.baseline_link_24), contentDescription = null)
                                },
                                modifier = Modifier.clickable {
                                    uriHandler.openUri(bottomSheetSource?.url ?: return@clickable)
                                    bottomSheetSource = null
                                }
                            )
                        }
                        ListItem(
                            headlineContent = { Text("Delete") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_delete_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                viewModel.deleteSource(bottomSheetSource?.id ?: return@clickable)
                                bottomSheetSource = null
                            }
                        )
                    }
                }
            }

            if(bottomSheetLog != null) {
                ModalBottomSheet(
                    onDismissRequest = { bottomSheetLog = null },
                    sheetState = logSheetState
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Delete") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_delete_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                viewModel.deleteLog(bottomSheetLog?.id ?: return@clickable)
                                bottomSheetLog = null
                            }
                        )
                    }
                }
            }
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


@Composable
fun Source(source: Source, modifier: Modifier = Modifier) {
    Card(
        modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(source.name, Modifier.padding(5.dp, 0.dp))
            Text(source.type.toString(), Modifier.padding(5.dp, 0.dp))
            Text(source.url, overflow = TextOverflow.Ellipsis, softWrap = false, modifier = Modifier.padding(5.dp, 0.dp))
        }
    }
}

@Composable
fun Log(log: Log, modifier: Modifier = Modifier) {
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)

    Card(
        modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp, 0.dp)
        ) {
            Row {
                Icon(painter = painterResource(id = R.drawable.baseline_star_rate_24), contentDescription = null)
                Text(text = log.stars.toString())
            }
            Text(text = log.source)
            Text(text = if(log.completed) "Completed" else "Uncompleted")
            Text(text = dateFormat.format(log.date))
        }
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