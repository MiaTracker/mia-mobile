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
    val index: List<MediaIndex>? = null,
)

abstract class IndexViewModel : ViewModel() {
    private val _state = MutableStateFlow(IndexState())
    val state: StateFlow<IndexState> = _state.asStateFlow()

    fun refresh(callback: () -> Unit) {
        viewModelScope.launch {
            val res = index()
            _state.update { state ->
                state.copy(
                    index = res.body() ?: emptyList(),
                )
            }
            callback()
        }
    }

    abstract suspend fun index(): Response<List<MediaIndex>>
    abstract fun title(): String
}

class MediaIndexViewModel : IndexViewModel() {
    override suspend fun index(): Response<List<MediaIndex>> {
        return Service.media.index()
    }

    override fun title(): String {
        return "All media"
    }
}

class MoviesIndexViewModel : IndexViewModel() {
    override suspend fun index(): Response<List<MediaIndex>> {
        return Service.movies.index()
    }

    override fun title(): String {
        return "Movies"
    }
}

class SeriesIndexViewModel : IndexViewModel() {
    override suspend fun index(): Response<List<MediaIndex>> {
        return Service.series.index()
    }

    override fun title(): String {
        return "Series"
    }
}