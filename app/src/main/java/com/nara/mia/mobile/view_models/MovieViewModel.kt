package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.LogCreate
import com.nara.mia.mobile.models.MovieDetails
import com.nara.mia.mobile.models.Source
import com.nara.mia.mobile.models.SourceCreate
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieState(
    val movie: MovieDetails? = null
)

class MovieViewModel(private val id: Int) : ViewModel(), IDetailsViewModel {
    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state.asStateFlow()

    override fun refresh(callback: () -> Unit) {
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

    override fun delete(navController: NavController) {
        viewModelScope.launch {
            Service.movies.delete(_state.value.movie?.id ?: return@launch)
            navController.popBackStack()
        }
    }

    override fun deleteSource(source: Int) {
        viewModelScope.launch {
            Service.movies.deleteSource(_state.value.movie?.id ?: return@launch, source)
            refresh { }
        }
    }

    override fun deleteLog(log: Int) {
        viewModelScope.launch {
            Service.movies.deleteLog(_state.value.movie?.id ?: return@launch, log)
            refresh { }
        }
    }

    override fun isSourceValid(source: Source): Boolean {
        return source.name.isNotEmpty() && source.url.isNotEmpty()
    }

    override fun saveSource(source: Source, callback: () -> Unit) {
        viewModelScope.launch {
            if(!isSourceValid(source)) return@launch
            if(source.id < 0)
                Service.movies.sourceCreate(state.value.movie?.id ?: return@launch, SourceCreate(
                    name = source.name,
                    url = source.url,
                    type = source.type
                ))
            else
                Service.movies.sourceUpdate(state.value.movie?.id ?: return@launch, source.id, source)
            refresh(callback)
        }
    }

    override fun isLogValid(log: Log): Boolean {
        return log.source.isNotEmpty() && (log.stars == null || log.stars in 0.0f..10.0f)
    }

    override fun saveLog(log: Log, callback: () -> Unit) {
        viewModelScope.launch {
            if(!isLogValid(log)) return@launch
            if(log.id < 0)
                Service.movies.logCreate(state.value.movie?.id ?: return@launch, LogCreate(
                    source = log.source,
                    date = log.date,
                    stars = log.stars,
                    completed = log.completed,
                    comment = log.comment
                ))
            else
                Service.movies.logUpdate(state.value.movie?.id ?: return@launch, log.id, log)
            refresh(callback)
        }
    }
}