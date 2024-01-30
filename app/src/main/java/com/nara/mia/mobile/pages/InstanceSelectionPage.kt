package com.nara.mia.mobile.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nara.mia.mobile.view_models.ConnectionState
import com.nara.mia.mobile.view_models.InstanceSelectionViewModel

@Composable
fun InstanceSelectionPage(viewModel: InstanceSelectionViewModel = viewModel(), innerPadding: PaddingValues) {
    val state = viewModel.state.collectAsState()
    Column(
        Modifier.padding(innerPadding),
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = state.value.url,
            onValueChange = { u -> viewModel.setUrl(u) },
            modifier = Modifier.fillMaxWidth(),
            isError = !state.value.isUrlValid,
            label = { Text(text = "Instance url") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Password icon")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { viewModel.test() },
                enabled = state.value.isUrlValid,
            ) {
                Text(text = "Test connection")
            }
            Button(
                onClick = { viewModel.connect() },
                enabled = state.value.isUrlValid
            ) {
                Text(text = "Connect")
            }
        }
        when (state.value.connectionState) {
            ConnectionState.Failed -> Text(text = "Failed to connect to instance with this url", color = Color.Red)
            ConnectionState.Connected -> Text(text = "Successfully connected", color = Color.Green)
            ConnectionState.Connecting -> Text(text = "Connecting...")
            else -> { }
        }
    }
}