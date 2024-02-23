package com.nara.mia.mobile.ui.components

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nara.mia.mobile.R
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.models.IMediaDetails
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.Source
import java.text.DateFormat
import java.time.Clock
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Logs(media: IMediaDetails, bottomSheetLog: MutableState<Log?>) {
    if(media.logs.any()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp, 5.dp)
        ) {
            Text(text = "Logs", style = MaterialTheme.typography.titleMedium)
            media.logs.forEach { l ->
                Log(l, Modifier.combinedClickable(onLongClick = {
                    bottomSheetLog.value = l
                }) { })
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogBottomModal(viewModel: IDetailsViewModel, bottomSheetLog: MutableState<Log?>, currentlyEditedLog: MutableState<Log?>) {
    val logSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { bottomSheetLog.value = null },
        sheetState = logSheetState
    ) {
        Column {
            ListItem(
                headlineContent = { Text("Edit") },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null)
                },
                modifier = Modifier.clickable {
                    currentlyEditedLog.value = bottomSheetLog.value
                    bottomSheetLog.value = null
                }
            )
            ListItem(
                headlineContent = { Text("Delete") },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.baseline_delete_24), contentDescription = null)
                },
                modifier = Modifier.clickable {
                    viewModel.deleteLog(bottomSheetLog.value?.id ?: return@clickable)
                    bottomSheetLog.value = null
                }
            )
        }
    }
}