package com.nara.mia.mobile.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nara.mia.mobile.view_models.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LoginPage(viewModel: LoginViewModel = viewModel(), innerPadding: PaddingValues) {
    val state = viewModel.state.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(10.dp)
    ) {
        TextField(
            value = state.value.username,
            onValueChange = { u -> viewModel.setUsername(u) },
            label = { Text(text = "Username") },
            isError = !state.value.usernameValid,
            leadingIcon = {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Username icon")
            },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = state.value.password,
            onValueChange = { p -> viewModel.setPassword(p) },
            label = { Text(text = "Password") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Password icon")
            },
            isError = !state.value.passwordValid,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { viewModel.changeInstance() },
            ) {
                Text(text = "Change instance")
            }
            Button(
                onClick = { viewModel.login() },
                enabled = state.value.credentialsEntered
            ) {
                Text(text = "Login")
            }
        }
    }
}