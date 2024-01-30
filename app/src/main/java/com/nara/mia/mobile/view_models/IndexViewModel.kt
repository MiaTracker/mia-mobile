package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

data class IndexState(
    val index: List<MediaIndex> = emptyList(),
    val isLoading: Boolean = false
)

abstract class IndexViewModel : ViewModel() {
    private val _state = MutableStateFlow(IndexState())
    val state: StateFlow<IndexState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update {
                _state.value.copy(isLoading = true)
            }
            val res = index()
            _state.update { state ->
                state.copy(
                    index = res.body() ?: emptyList(),
                    isLoading = false
                )
            }
        }
    }

    abstract suspend fun index(): Response<List<MediaIndex>>
}

class MediaIndexViewModel : IndexViewModel() {
    override suspend fun index(): Response<List<MediaIndex>> {
        return Service.media.index()
    }
}

class MoviesIndexViewModel : IndexViewModel() {
    override suspend fun index(): Response<List<MediaIndex>> {
        return Service.movies.index()
    }
}

class SeriesIndexViewModel : IndexViewModel() {
    override suspend fun index(): Response<List<MediaIndex>> {
        return Service.series.index()
    }

}