package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.mia.mobile.models.SeriesDetails
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SeriesState(
    val series: SeriesDetails? = null,
)

class SeriesViewModel(private val id: Int) : ViewModel() {
    private val _state = MutableStateFlow(SeriesState())
    val state: StateFlow<SeriesState> = _state.asStateFlow()

    fun refresh(callback: () -> Unit) {
        viewModelScope.launch {
            val res = Service.series.details(id)
            _state.update { state ->
                state.copy(
                    series = res.body(),
                )
            }
            callback()
        }
    }
}