package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
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

class SeriesViewModel(private val id: Int) : ViewModel(), IDetailsViewModel {
    private val _state = MutableStateFlow(SeriesState())
    val state: StateFlow<SeriesState> = _state.asStateFlow()

    override fun refresh(callback: () -> Unit) {
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

    override fun delete(navController: NavController) {
        viewModelScope.launch {
            Service.series.delete(_state.value.series?.id ?: return@launch)
            navController.popBackStack()
        }
    }
}