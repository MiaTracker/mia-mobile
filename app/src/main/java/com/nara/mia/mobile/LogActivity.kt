package com.nara.mia.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.infrastructure.PrefDataStore
import com.nara.mia.mobile.infrastructure.isInstanceUrlInitialized
import com.nara.mia.mobile.infrastructure.isTokenPresent
import com.nara.mia.mobile.pages.LogPage
import com.nara.mia.mobile.services.Http
import com.nara.mia.mobile.services.Service
import com.nara.mia.mobile.ui.theme.MiaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayLoading()
    }

    private fun displayLoading() {
        setContent {
            MiaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            Modifier.size(60.dp)
                        )
                    }

                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(key1 = "") {
                        coroutineScope.launch(Dispatchers.IO) {
                            load()
                        }
                    }
                }
            }
        }
    }

    private fun displayContent() {
        setContent {
            MiaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LogPage{ finish() }
                }
            }
        }
    }

    private suspend fun load() {
        Config.init(PrefDataStore.get(baseContext)) {
            if (isInstanceUrlInitialized()) {
                val connected = runBlocking {
                    Http.testConnection(Config.run?.instance)
                }
                if(connected) {
                    Service.init()
                    if(isTokenPresent()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val res = Service.configuration.images()
                            Config.images = res.body()
                            displayContent()
                        }
                    }
                    else finish()
                } else finish()
            } else {
                finish()
            }
        }
    }
}