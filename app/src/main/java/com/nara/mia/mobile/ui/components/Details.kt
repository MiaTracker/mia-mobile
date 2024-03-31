package com.nara.mia.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.nara.mia.mobile.R
import com.nara.mia.mobile.enums.SourceType
import com.nara.mia.mobile.enums.TagType
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.infrastructure.tmdbImagePainter
import com.nara.mia.mobile.models.AlternativeTitle
import com.nara.mia.mobile.models.Genre
import com.nara.mia.mobile.models.IMediaDetails
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.Source
import com.nara.mia.mobile.models.Tag
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Details(media: IMediaDetails?, navController: NavController, viewModel: IDetailsViewModel, specifics: @Composable () -> Unit) {
    val pullRefreshState = rememberPullToRefreshState()
    var menuExposed by remember { mutableStateOf(false) }
    var deleteDialogOpen by remember { mutableStateOf(false) }
    val bottomSheetSource = remember { mutableStateOf<Source?>(null) }
    val bottomSheetLog = remember { mutableStateOf<Log?>(null) }
    val bottomSheetTitle = remember { mutableStateOf<AlternativeTitle?>(null) }
    val bottomSheetGenre = remember { mutableStateOf<Genre?>(null) }
    val bottomSheetTag = remember { mutableStateOf<Tag?>(null) }
    val currentlyEditedSource = remember { mutableStateOf<Source?>(null) }
    val currentlyEditedLog = remember { mutableStateOf<Log?>(null) }
    val currentlyCreatedTagType = remember { mutableStateOf<TagType?>(null) }

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
                            text = { Text("Add source") },
                            onClick = {
                                currentlyEditedSource.value = Source(-1, "", "", SourceType.Web)
                                menuExposed = false
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_log_tile_icon),
                                    contentDescription = ""
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add log") },
                            onClick = {
                                currentlyEditedLog.value = Log(-1, Date(), "", null, true, null)
                                menuExposed = false
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_log_tile_icon),
                                    contentDescription = ""
                                )
                            }
                        )

                        if(media?.onWatchlist == true) {
                            DropdownMenuItem(
                                text = { Text("Remove from watchlist") },
                                onClick = {
                                    viewModel.removeFromWatchlist()
                                    menuExposed = false
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = ""
                                    )
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Add to watchlist") },
                                onClick = {
                                    viewModel.addToWatchlist()
                                    menuExposed = false
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_bookmark_24),
                                        contentDescription = ""
                                    )
                                }
                            )
                        }

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
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if(!media.backdropPath.isNullOrEmpty()) {
                        Image(
                            painter = tmdbImagePainter(media.backdropPath),
                            contentDescription = "Backdrop image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = tmdbImagePainter(media.posterPath),
                            contentDescription = "Backdrop image",
                            Modifier
                                .width(150.dp)
                                .height(225.dp)
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
                                    modifier = Modifier
                                        .absolutePadding(160.dp)
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                ) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        media.alternativeTitles.forEach { t ->
                                            SuggestionChip(
                                                onClick = { bottomSheetTitle.value = t },
                                                label = { Text(t.title) })
                                        }
                                    }

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        media.genres.forEach { g ->
                                            SuggestionChip(
                                                onClick = { bottomSheetGenre.value = g },
                                                label = { Text(g.name) })
                                        }
                                    }

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        media.tags.forEach { t ->
                                            SuggestionChip(
                                                onClick = { bottomSheetTag.value = t },
                                                label = { Text(t.name) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(10.dp, 0.dp).absoluteOffset(0.dp, (-5).dp)
                    ) {
                        SuggestionChip(
                            onClick = { currentlyCreatedTagType.value = TagType.Title },
                            label = { Text("Title") },
                            icon = {
                                Icon(painter = painterResource(id = R.drawable.add_log_tile_icon), contentDescription = null)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, labelColor = MaterialTheme.colorScheme.onTertiaryContainer)
                        )

                        SuggestionChip(
                            onClick = { currentlyCreatedTagType.value = TagType.Genre },
                            label = { Text("Genre") },
                            icon = {
                                Icon(painter = painterResource(id = R.drawable.add_log_tile_icon), contentDescription = null)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, labelColor = MaterialTheme.colorScheme.onTertiaryContainer)
                        )

                        SuggestionChip(
                            onClick = { currentlyCreatedTagType.value = TagType.Tag },
                            label = { Text("Tag") },
                            icon = {
                                Icon(painter = painterResource(id = R.drawable.add_log_tile_icon), contentDescription = null)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, labelColor = MaterialTheme.colorScheme.onTertiaryContainer)
                        )
                    }

                    Text(text = media.overview ?: "", Modifier.padding(10.dp, 5.dp))

                    Sources(media = media, bottomSheetSource = bottomSheetSource)
                    
                    Logs(media = media, bottomSheetLog = bottomSheetLog)
                }
            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f),
            )

            if(bottomSheetSource.value != null && media != null) {
                SourceBottomModal(
                    viewModel = viewModel,
                    bottomSheetSource = bottomSheetSource,
                    currentlyEditedSource = currentlyEditedSource
                )
            }

            if(bottomSheetLog.value != null) {
                LogBottomModal(
                    viewModel = viewModel,
                    bottomSheetLog = bottomSheetLog,
                    currentlyEditedLog = currentlyEditedLog
                )
            }

            if(bottomSheetTitle.value != null) {
                val titleSheetState = rememberModalBottomSheetState()
                ModalBottomSheet(
                    onDismissRequest = { bottomSheetTitle.value = null },
                    sheetState = titleSheetState
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Set primary") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_check_circle_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                viewModel.setPrimaryTitle(bottomSheetTitle.value?.id ?: return@clickable)
                                bottomSheetTitle.value = null
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Delete") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_delete_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                viewModel.deleteTitle(bottomSheetTitle.value?.id ?: return@clickable)
                                bottomSheetTitle.value = null
                            }
                        )
                    }
                }
            }

            if(bottomSheetGenre.value != null) {
                val genreSheetState = rememberModalBottomSheetState()
                ModalBottomSheet(
                    onDismissRequest = { bottomSheetGenre.value = null },
                    sheetState = genreSheetState
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Delete") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_delete_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                viewModel.deleteGenre(bottomSheetGenre.value?.id ?: return@clickable)
                                bottomSheetGenre.value = null
                            }
                        )
                    }
                }
            }

            if(bottomSheetTag.value != null) {
                val tagSheetState = rememberModalBottomSheetState()
                ModalBottomSheet(
                    onDismissRequest = { bottomSheetTag.value = null },
                    sheetState = tagSheetState
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Delete") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_delete_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                viewModel.deleteTag(bottomSheetTag.value?.id ?: return@clickable)
                                bottomSheetTag.value = null
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

    val source = currentlyEditedSource.value
    if(source != null) {
        SourceDialog(source = source, viewModel,
            updateSource = { currentlyEditedSource.value = it },
            dismiss = { currentlyEditedSource.value = null }
        )
    }

    val log = currentlyEditedLog.value
    if(log != null && media?.sources != null) {
        LogDialog(log = log, sources = media.sources, viewModel = viewModel,
            updateLog = { currentlyEditedLog.value = it }, dismiss = { currentlyEditedLog.value = null }
        )
    }

    val tagType = currentlyCreatedTagType.value
    if(tagType != null) {
        TagCreatePopup(create = {
            when(tagType) {
                TagType.Title -> viewModel.createTitle(it)
                TagType.Genre -> viewModel.createGenre(it)
                TagType.Tag -> viewModel.createTag(it)
            }
            currentlyCreatedTagType.value = null
        }) {
            currentlyCreatedTagType.value = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagCreatePopup(create: (String) -> Unit, dismiss: () -> Unit) {
    val text = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    ModalBottomSheet(onDismissRequest = { dismiss() }) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            TextField(value = text.value, onValueChange = { text.value = it }, modifier = Modifier
                .weight(1.0f)
                .focusRequester(focusRequester))
            FilledIconButton(onClick = { create(text.value) }, enabled = text.value.isNotEmpty(), modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(painter = painterResource(id = R.drawable.baseline_check_24), contentDescription = null)
            }
        }
    }

    LaunchedEffect(key1 = "") {
        focusRequester.requestFocus()
    }
}