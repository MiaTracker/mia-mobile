package com.nara.mia.mobile.ui.components

import android.os.Build
import android.webkit.URLUtil
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nara.mia.mobile.R
import com.nara.mia.mobile.enums.SourceType
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.infrastructure.imageUrl
import com.nara.mia.mobile.models.IMediaDetails
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.Source
import java.text.DateFormat
import java.time.Clock
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
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
    var currentlyEditedSource by remember { mutableStateOf<Source?>(null) }
    var currentlyEditedLog by remember { mutableStateOf<Log?>(null) }

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
                                currentlyEditedSource = Source(-1, "", "", SourceType.Web)
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
                                currentlyEditedLog = Log(-1, Date(), "", null, true, null)
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
                            headlineContent = { Text("Edit") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                if(media == null) return@clickable
                                currentlyEditedSource = bottomSheetSource
                                bottomSheetSource = null
                            }
                        )
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
                            headlineContent = { Text("Edit") },
                            leadingContent = {
                                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                currentlyEditedLog = bottomSheetLog
                                bottomSheetLog = null
                            }
                        )
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

    val source = currentlyEditedSource
    if(source != null) {
        SourceDialog(source = source, viewModel,
            updateSource = { currentlyEditedSource = it },
            dismiss = { currentlyEditedSource = null }
        )
    }

    val log = currentlyEditedLog
    if(log != null && media?.sources != null) {
        LogDialog(log = log, sources = media.sources, viewModel = viewModel,
            updateLog = { currentlyEditedLog = it }, dismiss = { currentlyEditedLog = null })
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp, 0.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row {
                    Icon(painter = painterResource(id = R.drawable.baseline_star_rate_24), contentDescription = null)
                    Text(text = log.stars.toString())
                }
                Text(text = log.source)
                Text(text = if(log.completed) "Completed" else "Uncompleted")
                Text(text = dateFormat.format(log.date))
            }
            if(!log.comment.isNullOrEmpty()) {
                Text(text = log.comment, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceDialog(source: Source, viewModel: IDetailsViewModel, updateSource: (Source) -> Unit, dismiss: () -> Unit) {
    var typeExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Dialog(onDismissRequest = { dismiss() }) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                TextField(
                    value = source.name,
                    onValueChange = { updateSource(source.copy( name = it )) },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                Box {
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it }
                    ) {
                        TextField(
                            value = source.type.toString(),
                            readOnly = true,
                            onValueChange = { },
                            label = { Text(text = "Type") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false },
                            Modifier.fillMaxWidth()
                        ) {
                            enumValues<SourceType>().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(text = type.toString()) },
                                    onClick = {
                                        updateSource(source.copy(
                                            type = type
                                        ))
                                        typeExpanded = false
                                    },
                                    Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Row {
                    TextField(
                        value = source.url,
                        onValueChange = { updateSource(source.copy(url = it)) },
                        label = { Text("Url") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = { dismiss() }) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { viewModel.saveSource(source) { dismiss() } },
                        enabled = viewModel.isSourceValid(source)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = "") {
        val id = source.id
        if(id < 0) focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDialog(log: Log, sources: List<Source>, viewModel: IDetailsViewModel, updateLog: (Log) -> Unit, dismiss: () -> Unit) {
    var sourceExpanded by remember { mutableStateOf(false) }
    var source by remember { mutableStateOf<Source?>(null) }
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    val datepickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Instant.ofEpochMilli(utcTimeMillis) <= Instant.now(Clock.systemUTC())
                } else {
                    return utcTimeMillis < Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
                }
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= Calendar.getInstance().get(Calendar.YEAR)
            }
        },
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis
    )
    var openDatePicker by remember { mutableStateOf(false) }
    val dateFieldInteractionSource = remember { MutableInteractionSource() }
    val dateFieldInteractionState by dateFieldInteractionSource.collectIsPressedAsState()
    var starsString by remember { mutableStateOf(if(log.stars == null) { "" } else { log.stars.toString() }) }

    LaunchedEffect(key1 = "") {
        if(log.source.isNotEmpty()) {
            source = sources.find { s -> s.name == log.source }
        }
        if(source == null && sources.count() == 1) source = sources.first()
        updateLog(log.copy(source = source?.name ?: ""))
    }


    Dialog(onDismissRequest = { dismiss() }) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = sourceExpanded,
                    onExpandedChange = { sourceExpanded = it }
                ) {
                    TextField(
                        value = source?.name ?: "",
                        readOnly = true,
                        onValueChange = { },
                        label = { Text(text = "Source") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = sourceExpanded,
                        onDismissRequest = { sourceExpanded = false },
                        Modifier.fillMaxWidth()
                    ) {
                        sources.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(text = s.name) },
                                onClick = {
                                    source = s
                                    sourceExpanded = false
                                },
                                Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                TextField(
                    value = dateFormat.format(log.date),
                    readOnly = true,
                    interactionSource = dateFieldInteractionSource,
                    onValueChange = { },
                    label = { Text(text = "Date") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = "Open calendar",
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    label = { Text(text = "Stars") },
                    value = starsString,
                    onValueChange = { v ->
                        starsString = if (v.isEmpty()) {
                            updateLog(log.copy(stars = null))
                            ""
                        } else {
                            when (val f = v.toFloatOrNull()) {
                                null -> starsString
                                else -> {
                                    updateLog(log.copy(stars = f))
                                    v
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth()

                )

                TextField(
                    label = { Text("Comment") },
                    value = log.comment ?: "",
                    onValueChange = { updateLog(log.copy(comment = it)) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .toggleable(
                                value = log.completed,
                                onValueChange = { updateLog(log.copy(completed = it)) },
                                role = Role.Checkbox
                            )
                    ) {
                        Checkbox(checked = log.completed, onCheckedChange = null)
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = { dismiss() }) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { viewModel.saveLog(log) { dismiss() } },
                        enabled = viewModel.isLogValid(log)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    when {
        openDatePicker -> {
            DatePickerDialog(
                onDismissRequest = { openDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val mills = datepickerState.selectedDateMillis
                        if(mills != null)
                            updateLog(log.copy(date = Date(mills)))
                        openDatePicker = false
                    }) {
                        Text(text = "OK")
                    }
                }
            ) {
                DatePicker(state = datepickerState)
            }
        }
        dateFieldInteractionState -> {
            openDatePicker = true
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