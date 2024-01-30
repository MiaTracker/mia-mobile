package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.services.Http
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ConnectionState {
    None,
    Connecting,
    Connected,
    Failed
}

data class InstanceSelectionState(

    val url: String = "",
    val isUrlValid: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.None
)

class InstanceSelectionViewModel(private val connectionCallback: () -> Unit) : ViewModel() {
    private val urlRegex = """(http|https)://.*\..+""".toRegex(RegexOption.IGNORE_CASE)
    private val _state = MutableStateFlow(InstanceSelectionState())
    val state: StateFlow<InstanceSelectionState> = _state.asStateFlow()

    init {
        setUrl(Config.run?.instance ?: "")
    }

    fun setUrl(url: String) {
        _state.update { state ->
            state.copy(
                url = url,
                isUrlValid = urlRegex matches url
            )
        }
    }

    fun connect() {
        if (!_state.value.isUrlValid) return
        viewModelScope.launch {

            val success = Http.testConnection(_state.value.url)
            if(!success) {
                _state.update { state ->
                    state.copy(connectionState = ConnectionState.Failed)
                }
                return@launch
            }

            Config.run?.setInstance(_state.value.url)
            connectionCallback()
        }
    }

    fun test() {
        if(!_state.value.isUrlValid) return
        _state.update { state ->
            state.copy(
                connectionState = ConnectionState.Connecting
            )
        }
        viewModelScope.launch {
            val success = Http.testConnection(_state.value.url)
            _state.update { state ->
                state.copy(
                    connectionState = if (success) ConnectionState.Connected else ConnectionState.Failed
                )
            }
        }
    }
}