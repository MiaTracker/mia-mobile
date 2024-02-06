package com.nara.mia.mobile.pages

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.nara.mia.mobile.infrastructure.imageUrl
import com.nara.mia.mobile.view_models.LogViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nara.mia.mobile.R
import com.nara.mia.mobile.enums.MediaType
import com.nara.mia.mobile.enums.SourceType
import com.nara.mia.mobile.models.IIndex
import java.time.Clock
import java.time.Instant
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPage(viewModel: LogViewModel = viewModel(), onClose: () -> Unit) {
    val state by viewModel.state.collectAsState()

    var openDialog by remember { mutableStateOf(false) }
    var sourceExpanded by remember { mutableStateOf(false) }
    var newSourceTypeExpanded by remember { mutableStateOf(false) }
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
    var starsString by remember { mutableStateOf(if(state.stars == null) { "" } else { state.stars.toString() }) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable {
                    openDialog = true
                }
        ) {
            if(state.index != null) {
                IndexListItem(
                    idx = state.index!!,
                    external = state.externalIndex,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                )
            } else {
                Text(
                    text = "Select media",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                )
            }
        }

        if(state.index != null && state.source == null) {
            HorizontalDivider()
        }
        Row(
            Modifier.fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = sourceExpanded,
                onExpandedChange = { if(state.index != null) sourceExpanded = it },
                Modifier.weight(1.0f)
            ) {
                TextField(
                    value = state.source?.name ?: "New source",
                    enabled = state.index != null,
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
                    state.sources?.forEach { source ->
                        DropdownMenuItem(
                            text = { Text(text = source.name) },
                            onClick = {
                                viewModel.sourceSelected(source)
                                sourceExpanded = false
                            },
                            Modifier.fillMaxWidth()
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(text = "New source") },
                        onClick = {
                            viewModel.sourceSelected(null)
                            sourceExpanded = false
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "New source")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            IconButton(
                onClick = { viewModel.refreshSources() },
                enabled = state.index != null,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        if(state.index != null && state.source == null) {
            TextField(
                value = state.newSourceName,
                onValueChange = { viewModel.setNewSourceName(it) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Box {
                ExposedDropdownMenuBox(
                    expanded = newSourceTypeExpanded,
                    onExpandedChange = { newSourceTypeExpanded = it }
                ) {
                    TextField(
                        value = state.newSourceType?.toString() ?: "",
                        readOnly = true,
                        onValueChange = { },
                        label = { Text(text = "Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = newSourceTypeExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = newSourceTypeExpanded,
                        onDismissRequest = { newSourceTypeExpanded = false },
                        Modifier.fillMaxWidth()
                    ) {
                        enumValues<SourceType>().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type.toString()) },
                                onClick = {
                                    viewModel.newSourceTypeSelected(type)
                                    newSourceTypeExpanded = false
                                },
                                Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Row {
                TextField(
                    value = state.newSourceUrl,
                    onValueChange = { viewModel.setNewSourceUrl(it) },
                    label = { Text("Url") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = state.dateString,
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
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            TextField(
                label = { Text(text = "Stars") },
                value = starsString,
                onValueChange = { v ->
                    starsString = if(v.isEmpty()) {
                        viewModel.setStars(null)
                        ""
                    } else {
                        when(val f = v.toFloatOrNull()) {
                            null -> starsString
                            else -> {
                                viewModel.setStars(f)
                                v
                            }
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )
        }

        TextField(
            label = { Text("Comment") },
            value = state.comment ?: "",
            onValueChange = { viewModel.setComment(it) },
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
                        value = state.completed,
                        onValueChange = { viewModel.setCompleted(it) },
                        role = Role.Checkbox
                    )
            ) {
                Checkbox(checked = state.completed, onCheckedChange = null)
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            
            if(state.onWatchlist) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .toggleable(
                            value = state.removeFromWatchlist,
                            onValueChange = { viewModel.setRemoveFromWatchlist(it) },
                            role = Role.Checkbox
                        )
                ) {
                    Checkbox(checked = state.removeFromWatchlist, onCheckedChange = null)
                    Text(
                        text = "Remove from watchlist",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = { onClose() }) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    viewModel.save()
                    onClose()
                },
                enabled = viewModel.filled()
            ) {
                Text("Save")
            }
        }
    }

    when {
        openDialog -> {
            MediaSelectionDialog(viewModel) {
                openDialog = false
            }
        }
        openDatePicker -> {
            DatePickerDialog(
                onDismissRequest = { openDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.dateSelected(datepickerState.selectedDateMillis)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSelectionDialog(viewModel: LogViewModel, dismiss: () -> Unit) {
    val state by viewModel.state.collectAsState()

    val searchActive = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    Dialog(onDismissRequest = { dismiss() }) {
        DockedSearchBar(
            query = state.mediaQuery ?: "",
            onQueryChange = { viewModel.mediaSearch(it) },
            onSearch = { viewModel.mediaSearch(it) },
            active = searchActive.value,
            onActiveChange = { searchActive.value = it },
            Modifier.focusRequester(focusRequester)
        ) {
            var count = 0 //TODO: remove when paging is implemented
            state.mediaResults?.indexes?.forEach { idx ->
                count++
                if(count > 5) return@forEach
                IndexListItem(idx = idx, external = false) {
                    viewModel.mediaSelected(idx)
                    dismiss()
                }
            }
            state.mediaResults?.external?.forEach { idx ->
                count++
                if(count > 5) return@forEach
                IndexListItem(idx = idx, external = true) {
                    viewModel.mediaSelected(idx)
                    dismiss()
                }
            }
        }
    }
}

@Composable
fun IndexListItem(idx: IIndex, external: Boolean, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val m = if(onClick != null) {
        modifier.clickable {
            onClick()
        }
    } else {
        modifier
    }

    ListItem(
        headlineContent = { Text(text = idx.title) },
        leadingContent = {
            if(idx.posterPath != null) {
                AsyncImage(
                    model = imageUrl(idx.posterPath!!),
                    contentDescription = "Poster",
                    Modifier.height(90.dp)
                )
            }
        },
        trailingContent = {
            Row {
                if(external) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_link_24),
                        contentDescription = "External",
                    )
                }
                if(idx.type == MediaType.Movie) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_movie_48),
                        contentDescription = "Movie",
                        Modifier.width(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_tv_48),
                        contentDescription = "Series",
                        Modifier.width(24.dp)
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = m
    )
}