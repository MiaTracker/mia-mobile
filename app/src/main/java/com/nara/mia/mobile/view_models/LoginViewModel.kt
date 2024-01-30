package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.infrastructure.StatusCode
import com.nara.mia.mobile.models.UserLogin
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val username: String = "",
    val password: String = "",
    val usernameValid: Boolean = true,
    val passwordValid: Boolean = true,
    val credentialsEntered: Boolean = false,
    val state: StateEnum = StateEnum.None
) {
    enum class StateEnum {
        None,
        IncorrectCredentials
    }
}

class LoginViewModel(private val loginCallback: () -> Unit, private val instanceChangeCallback: () -> Unit) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun setUsername(username: String) {
        _state.update { state ->
            state.copy(
                username = username,
                usernameValid = username.isNotEmpty(),
                credentialsEntered = username.isNotEmpty() && state.password.isNotEmpty()
            )
        }
    }

    fun setPassword(password: String) {
        _state.update { state ->
            state.copy(
                password = password,
                passwordValid = password.isNotEmpty(),
                credentialsEntered = state.username.isNotEmpty() && password.isNotEmpty()
            )
        }
    }

    fun login() {
        if(_state.value.username.isEmpty() || _state.value.password.isEmpty()) return
        viewModelScope.launch {
            val user = UserLogin(_state.value.username, _state.value.password)
            val res = Service.users.login(user)
            if(res.isSuccessful) {
                val token = res.body() ?: return@launch  //TODO: handle
                _state.update { state ->
                    state.copy(
                        state = LoginState.StateEnum.None
                    )
                }
                Config.run?.setToken(token)
                loginCallback()
            } else if(res.code() == StatusCode.Unauthorized.code) {
                _state.update { state ->
                    state.copy(
                        state = LoginState.StateEnum.IncorrectCredentials
                    )
                }
            } else {
                //TODO: handling unexpected errors
            }
        }
    }

    fun changeInstance() {
        instanceChangeCallback()
    }
}