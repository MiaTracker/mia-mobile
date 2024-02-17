package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.mia.mobile.models.MovieDetails
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieState(
    val movie: MovieDetails? = null
)

class MovieViewModel(private val id: Int) : ViewModel() {
    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state.asStateFlow()

    init {
        refresh { }
    }

    fun refresh(callback: () -> Unit) {
        viewModelScope.launch {
            val res = Service.movies.details(id)
            _state.update { state ->
                state.copy(
                    movie = res.body(),
                )
            }
            callback()
        }
    }
}