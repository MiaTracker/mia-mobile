package com.nara.mia.mobile.view_models

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.mia.mobile.infrastructure.Config
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

open class BaseViewModel : ViewModel() {
    val snackbarHostState = SnackbarHostState()

    protected fun handleErrors(err: ResponseBody) {
        val json = err.string()
        Log.d(Config.const.loggingTag, "Api responded with an error: $json")
        viewModelScope.launch {
            snackbarHostState.showSnackbar("An error occurred while talking to the server.")
        }
    }
}