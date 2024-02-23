package com.nara.mia.mobile.ui.components

import android.webkit.URLUtil
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nara.mia.mobile.R
import com.nara.mia.mobile.enums.SourceType
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.models.IMediaDetails
import com.nara.mia.mobile.models.Source

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Sources(media: IMediaDetails, bottomSheetSource: MutableState<Source?>) {
    if(media.sources.any()) {
        val uriHandler = LocalUriHandler.current

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp, 5.dp)
        ) {
            Text(text = "Sources", style = MaterialTheme.typography.titleMedium)
            media.sources.forEach { s ->
                Source(s, Modifier.combinedClickable(onLongClick = {
                    bottomSheetSource.value = s
                }) {
                    if(URLUtil.isValidUrl(s.url))
                        uriHandler.openUri(s.url)
                })
            }
        }
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
fun SourceBottomModal(viewModel: IDetailsViewModel, bottomSheetSource: MutableState<Source?>, currentlyEditedSource: MutableState<Source?>) {
    val sourceSheetState = rememberModalBottomSheetState()
    val uriHandler = LocalUriHandler.current

    ModalBottomSheet(
        onDismissRequest = { bottomSheetSource.value = null },
        sheetState = sourceSheetState
    ) {
        Column {
            if(URLUtil.isValidUrl(bottomSheetSource.value?.url)) {
                ListItem(
                    headlineContent = { Text("Open link") },
                    leadingContent = {
                        Icon(painter = painterResource(id = R.drawable.baseline_link_24), contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        uriHandler.openUri(bottomSheetSource.value?.url ?: return@clickable)
                        bottomSheetSource.value = null
                    }
                )
            }
            ListItem(
                headlineContent = { Text("Edit") },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = null)
                },
                modifier = Modifier.clickable {
                    currentlyEditedSource.value = bottomSheetSource.value
                    bottomSheetSource.value = null
                }
            )
            ListItem(
                headlineContent = { Text("Delete") },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.baseline_delete_24), contentDescription = null)
                },
                modifier = Modifier.clickable {
                    viewModel.deleteSource(bottomSheetSource.value?.id ?: return@clickable)
                    bottomSheetSource.value = null
                }
            )
        }
    }
}