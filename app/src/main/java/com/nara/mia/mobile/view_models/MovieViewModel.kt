package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.models.GenreCreate
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.LogCreate
import com.nara.mia.mobile.models.MovieDetails
import com.nara.mia.mobile.models.Source
import com.nara.mia.mobile.models.SourceCreate
import com.nara.mia.mobile.models.TagCreate
import com.nara.mia.mobile.models.TitleCreate
import com.nara.mia.mobile.models.WatchlistParams
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            Service.movies.delete(_state.value.movie?.id ?: return@launch)
            viewModelScope.launch(Dispatchers.Main) {
                navController.popBackStack()
            }
        }
    }

    override fun deleteSource(source: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Service.movies.deleteSource(_state.value.movie?.id ?: return@launch, source)
            refresh { }
        }
    }

    override fun deleteLog(log: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Service.movies.deleteLog(_state.value.movie?.id ?: return@launch, log)
            refresh { }
        }
    }

    override fun isSourceValid(source: Source): Boolean {
        return source.name.isNotEmpty() && source.url.isNotEmpty()
    }

    override fun saveSource(source: Source, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
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

    override fun createTitle(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(title.isEmpty()) return@launch
            Service.movies.titleCreate(state.value.movie?.id ?: return@launch, TitleCreate(title))
            refresh { }
        }
    }

    override fun setPrimaryTitle(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Service.movies.titleSetPrimary(state.value.movie?.id ?: return@launch, id)
            refresh { }
        }
    }

    override fun deleteTitle(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Service.movies.titleDelete(state.value.movie?.id ?: return@launch, id)
            refresh { }
        }
    }

    override fun createGenre(genre: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(genre.isEmpty()) return@launch
            Service.movies.genreCreate(state.value.movie?.id ?: return@launch, GenreCreate(genre))
            refresh { }
        }
    }

    override fun deleteGenre(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Service.movies.genreDelete(state.value.movie?.id ?: return@launch, id)
            refresh { }
        }
    }

    override fun createTag(tag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(tag.isEmpty()) return@launch
            Service.movies.tagCreate(state.value.movie?.id ?: return@launch, TagCreate(tag))
            refresh { }
        }
    }

    override fun deleteTag(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Service.movies.tagDelete(state.value.movie?.id ?: return@launch, id)
            refresh { }
        }
    }

    override fun addToWatchlist() {
        viewModelScope.launch(Dispatchers.IO) {
            Service.watchlist.add(WatchlistParams(state.value.movie?.id ?: return@launch))
            refresh {  }
        }
    }

    override fun removeFromWatchlist() {
        viewModelScope.launch(Dispatchers.IO) {
            Service.watchlist.remove(WatchlistParams(state.value.movie?.id ?: return@launch))
            refresh {  }
        }
    }
}