package com.nara.mia.mobile.view_models

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

class MovieViewModel(private val id: Int) : BaseViewModel(), IDetailsViewModel {
    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state.asStateFlow()

    override fun refresh(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.details(id)
            res.errorBody()?.let { err -> handleErrors(err) }
            res.body()?.let { movie ->
                _state.update { state ->
                    state.copy(
                        movie = movie
                    )
                }
                callback()
            }
        }
    }

    override fun delete(navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.delete(_state.value.movie?.id ?: return@launch)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                viewModelScope.launch(Dispatchers.Main) {
                    navController.popBackStack()
                }
            }
        }
    }

    override fun deleteSource(source: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.deleteSource(_state.value.movie?.id ?: return@launch, source)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun deleteLog(log: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.deleteLog(_state.value.movie?.id ?: return@launch, log)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun isSourceValid(source: Source): Boolean {
        return source.name.isNotEmpty() && source.url.isNotEmpty()
    }

    override fun saveSource(source: Source, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!isSourceValid(source)) return@launch
            val res = if(source.id < 0)
                Service.movies.sourceCreate(state.value.movie?.id ?: return@launch, SourceCreate(
                    name = source.name,
                    url = source.url,
                    type = source.type
                ))
            else
                Service.movies.sourceUpdate(state.value.movie?.id ?: return@launch, source.id, source)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh(callback)
            }
        }
    }

    override fun isLogValid(log: Log): Boolean {
        return log.source.isNotEmpty() && (log.stars == null || log.stars in 0.0f..10.0f)
    }

    override fun saveLog(log: Log, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!isLogValid(log)) return@launch
            val res = if(log.id < 0)
                Service.movies.logCreate(state.value.movie?.id ?: return@launch, LogCreate(
                    source = log.source,
                    date = log.date,
                    stars = log.stars,
                    completed = log.completed,
                    comment = log.comment
                ))
            else
                Service.movies.logUpdate(state.value.movie?.id ?: return@launch, log.id, log)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh(callback)
            }
        }
    }

    override fun createTitle(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(title.isEmpty()) return@launch
            val res = Service.movies.titleCreate(state.value.movie?.id ?: return@launch, TitleCreate(title))
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun setPrimaryTitle(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.titleSetPrimary(state.value.movie?.id ?: return@launch, id)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun deleteTitle(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.titleDelete(state.value.movie?.id ?: return@launch, id)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun createGenre(genre: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(genre.isEmpty()) return@launch
            val res = Service.movies.genreCreate(state.value.movie?.id ?: return@launch, GenreCreate(genre))
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun deleteGenre(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.genreDelete(state.value.movie?.id ?: return@launch, id)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun createTag(tag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(tag.isEmpty()) return@launch
            val res = Service.movies.tagCreate(state.value.movie?.id ?: return@launch, TagCreate(tag))
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun deleteTag(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.movies.tagDelete(state.value.movie?.id ?: return@launch, id)
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun addToWatchlist() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.watchlist.add(WatchlistParams(state.value.movie?.id ?: return@launch))
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }

    override fun removeFromWatchlist() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Service.watchlist.remove(WatchlistParams(state.value.movie?.id ?: return@launch))
            res.errorBody()?.let { err -> handleErrors(err) }
            if(res.isSuccessful) {
                refresh { }
            }
        }
    }
}