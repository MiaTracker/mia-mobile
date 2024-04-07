package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.nara.mia.mobile.enums.MediaType
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.models.ExternalIndex
import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SearchQuery
import com.nara.mia.mobile.models.SearchResults
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

data class IndexState(
    val index: List<MediaIndex>? = null,
    val external: List<ExternalIndex>? = null,
    val query: String = "",
    val committed: Boolean = false
)

abstract class IndexViewModel : ViewModel() {
    private val _state = MutableStateFlow(IndexState())
    val state: StateFlow<IndexState> = _state.asStateFlow()

    abstract val multiType: Boolean

    init {
        if(Config.images == null) {
            runBlocking {
                val res = Service.configuration.images()
                Config.images = res.body()
            }
        }
    }

    fun refresh(callback: (() -> Unit)? = null) {
        if(state.value.query.isEmpty()) index(callback)
        else search(callback)
    }

    fun applySearch(query: String, committed: Boolean = false) {
        _state.update { state ->
            state.copy(
                query = query,
                committed = committed
            )
        }
        refresh()
    }

    fun create(idx: ExternalIndex, navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            if(idx.type == MediaType.Movie) {
                val res = Service.movies.create(idx.externalId) //TODO: handle
                res.body()?.let { id -> navController.navigate("movie/${id}") }
            } else {
                val res = Service.series.create(idx.externalId) //TODO: handle
                res.body()?.let { id -> navController.navigate("series/${id}") }
            }
            refresh()
        }
    }

    private fun index(callback: (() -> Unit)?) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = apiIndex()
            _state.update { state ->
                state.copy(
                    index = res.body() ?: emptyList(),
                )
            }
            callback?.invoke()
        }
    }

    private fun search(callback: (() -> Unit)?) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = apiSearch(state.value.query, state.value.committed)
            res.body()?.let { data ->
                _state.update { state ->
                    state.copy(
                        index = data.indexes,
                        external = data.external
                    )
                }
            }

            callback?.invoke()
        }
    }

    abstract suspend fun apiIndex(): Response<List<MediaIndex>>
    abstract suspend fun apiSearch(query: String, committed: Boolean): Response<SearchResults>
    abstract fun title(): String
}

class MediaIndexViewModel : IndexViewModel() {
    override val multiType: Boolean = true

    override suspend fun apiIndex(): Response<List<MediaIndex>> {
        return Service.media.index()
    }

    override suspend fun apiSearch(query: String, committed: Boolean): Response<SearchResults> {
        return Service.media.search(committed, null, SearchQuery(query, null, false, null))
    }

    override fun title(): String {
        return "All media"
    }
}

class MoviesIndexViewModel : IndexViewModel() {
    override val multiType: Boolean = false

    override suspend fun apiIndex(): Response<List<MediaIndex>> {
        return Service.movies.index()
    }

    override suspend fun apiSearch(query: String, committed: Boolean): Response<SearchResults> {
        return Service.movies.search(committed, SearchQuery(query, null, false, null))
    }

    override fun title(): String {
        return "Movies"
    }
}

class SeriesIndexViewModel : IndexViewModel() {
    override val multiType: Boolean = false

    override suspend fun apiIndex(): Response<List<MediaIndex>> {
        return Service.series.index()
    }

    override suspend fun apiSearch(query: String, committed: Boolean): Response<SearchResults> {
        return Service.series.search(committed, SearchQuery(query, null, false, null))
    }

    override fun title(): String {
        return "Series"
    }
}

class WatchlistViewModel : IndexViewModel() {
    override val multiType: Boolean = true

    override suspend fun apiIndex(): Response<List<MediaIndex>> {
        return Service.watchlist.index()
    }

    override suspend fun apiSearch(query: String, committed: Boolean): Response<SearchResults> {
        return Service.watchlist.search(SearchQuery(query, null, false, null))
    }

    override fun title(): String {
        return "Watchlist"
    }
}