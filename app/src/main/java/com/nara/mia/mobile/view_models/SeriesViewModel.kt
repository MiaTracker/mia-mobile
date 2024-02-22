package com.nara.mia.mobile.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.nara.mia.mobile.infrastructure.IDetailsViewModel
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.LogCreate
import com.nara.mia.mobile.models.SeriesDetails
import com.nara.mia.mobile.models.Source
import com.nara.mia.mobile.models.SourceCreate
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

    override fun deleteSource(source: Int) {
        viewModelScope.launch {
            Service.series.deleteSource(_state.value.series?.id ?: return@launch, source)
            refresh { }
        }
    }

    override fun deleteLog(log: Int) {
        viewModelScope.launch {
            Service.series.deleteLog(_state.value.series?.id ?: return@launch, log)
            refresh { }
        }
    }

    override fun isSourceValid(source: Source): Boolean {
        return source.name.isNotEmpty() && source.url.isNotEmpty()
    }

    override fun saveSource(source: Source, callback: () -> Unit) {
        viewModelScope.launch {
            if(source.id < 0)
                Service.series.sourceCreate(state.value.series?.id ?: return@launch, SourceCreate(
                    name = source.name,
                    url = source.url,
                    type = source.type
                ))
            else
                Service.series.sourceUpdate(state.value.series?.id ?: return@launch, source.id, source)
            refresh(callback)
        }
    }

    override fun isLogValid(log: Log): Boolean {
        return log.source.isNotEmpty()
    }

    override fun saveLog(log: Log, callback: () -> Unit) {
        viewModelScope.launch {
            if(!isLogValid(log)) return@launch
            if(log.id < 0)
                Service.series.logCreate(state.value.series?.id ?: return@launch, LogCreate(
                    source = log.source,
                    date = log.date,
                    stars = log.stars,
                    completed = log.completed,
                    comment = log.comment
                ))
            else
                Service.series.logUpdate(state.value.series?.id ?: return@launch, log.id, log)
            refresh(callback)
        }
    }
}