package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.ObjectMapper
import com.nara.mia.mobile.enums.MediaType
import com.nara.mia.mobile.models.ExternalIndex
import com.nara.mia.mobile.models.IIndex
import com.nara.mia.mobile.models.Logset
import com.nara.mia.mobile.models.MediaIndex
import com.nara.mia.mobile.models.SearchResults
import com.nara.mia.mobile.models.Source
import com.nara.mia.mobile.services.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Vector

data class LogState(
    val index: IIndex? = null,
    val externalIndex: Boolean = false,
    val mediaQuery: String? = null,
    val mediaResults: SearchResults? = null,
    val source: Source? = null,
    val sources: Vector<Source>? = null,
    val dateString: String = "",
    val date: Date? = null,
    val stars: Float? = null,
    val completed: Boolean = true,
    val onWatchlist: Boolean = false,
    val removeFromWatchlist: Boolean = true,
    val comment: String? = null
)

class LogViewModel : ViewModel() {
    private val _state = MutableStateFlow(LogState())
    val state: StateFlow<LogState> = _state.asStateFlow()

    val dateFormat = DateFormat.getDateInstance()

    init {
        mediaSearch("")
    }

    fun mediaSearch(query: String) {
        _state.update { state ->
            state.copy(mediaQuery = query)
        }
        viewModelScope.launch {
            val res = Service.media.search(query)
            if(!res.isSuccessful) return@launch //TODO: handle
            _state.update { state ->
                state.copy(
                    mediaResults = res.body()
                )
            }
        }
    }

    fun mediaSelected(index: IIndex) {
        _state.update { state ->
            state.copy(
                index = index,
                externalIndex = index is ExternalIndex
            )
        }
        refreshSources()
        refreshOnWatchlist()
    }

    fun sourceSelected(source: Source) {
        _state.update { state ->
            state.copy(
                source = source
            )
        }
    }

    fun refreshSources() {
        viewModelScope.launch {
            val idx = state.value.index
            if(idx !is MediaIndex) return@launch
            val res = if(state.value.index!!.type == MediaType.Movie) Service.movies.sources(idx.id)
                else Service.series.sources(idx.id)
            val sources = res.body()
            if(!res.isSuccessful || sources == null) return@launch //TODO: handle
            val source = if (_state.value.source != null && sources.any { x -> x.id == _state.value.source?.id }) {
                _state.value.source
            } else if(sources.count() == 1) { sources[0] }
            else { null }
            _state.update { state ->
                state.copy(
                    sources = sources,
                    source = source
                )
            }
        }
    }

    fun refreshOnWatchlist() {
        viewModelScope.launch {
            val idx = state.value.index
            if(idx !is MediaIndex) return@launch
            val res = if(state.value.index!!.type == MediaType.Movie) Service.movies.onWatchlist(idx.id)
            else Service.series.onWatchlist(idx.id)
            val onWatchlist = res.body()
            if(!res.isSuccessful || onWatchlist == null) return@launch //TODO: handle
            if(onWatchlist != state.value.onWatchlist) {
                _state.update { state ->
                    state.copy(
                        onWatchlist = onWatchlist
                    )
                }
            }
        }
    }

    fun dateSelected(dateInMillis: Long?) {
        if(dateInMillis == null) {
            _state.update { state ->
                state.copy(
                    dateString = "",
                    date = null
                )
            }
            return
        }
        val date = Date(dateInMillis)
        val str = dateFormat.format(date)
        _state.update { state ->
            state.copy(
                date = date,
                dateString = str
            )
        }
    }

    fun setStars(stars: Float?) {
        if(stars != null && (stars > 10.0f || stars < 0.0f)) return
        _state.update { state ->
            state.copy(
                stars = stars
            )
        }
    }

    fun setCompleted(completed: Boolean) {
        _state.update { state ->
            state.copy(
                completed = completed
            )
        }
    }

    fun setRemoveFromWatchlist(remove: Boolean) {
        _state.update { state ->
            state.copy(
                removeFromWatchlist = remove
            )
        }
    }

    fun setComment(comment: String) {
        _state.update { state ->
            state.copy(
                comment = comment.ifEmpty { null }
            )
        }
    }

    fun filled(): Boolean {
        return state.value.index != null && state.value.source != null && state.value.date != null
    }

    fun save() {
        viewModelScope.launch {
            if(!filled()) return@launch
            val model = Logset(
                mediaId = if(state.value.index is MediaIndex) { (state.value.index as MediaIndex).id } else { null },
                externalId = if(state.value.index is ExternalIndex) { (state.value.index as ExternalIndex).externalId } else { null },
                mediaType = state.value.index!!.type,
                source = if(state.value.source != null) { state.value.source!!.name } else { null },
                newSource = null,
                date = state.value.date!!,
                stars = state.value.stars,
                completed = state.value.completed,
                comment = state.value.comment,
                removeFromWatchlist = if(state.value.onWatchlist) { state.value.removeFromWatchlist } else { null }
            )

            val y = ExternalIndex(
                externalId = 3,
                type = MediaType.Movie,
                posterPath = "sadf",
                title = "asdf"
            )

            val mapper = ObjectMapper()
            val x = mapper.writeValueAsString(model)
            val z = mapper.writeValueAsString(y)
            println(x)
            println(z)

            val res = Service.logset.create(model)
            if(!res.isSuccessful) return@launch //TODO: handle
        }
    }
}