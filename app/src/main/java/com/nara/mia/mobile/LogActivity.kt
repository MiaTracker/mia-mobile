package com.nara.mia.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.infrastructure.PrefDataStore
import com.nara.mia.mobile.infrastructure.isInstanceUrlInitialized
import com.nara.mia.mobile.infrastructure.isTokenPresent
import com.nara.mia.mobile.pages.LogPage
import com.nara.mia.mobile.services.Http
import com.nara.mia.mobile.services.Service
import com.nara.mia.mobile.ui.theme.MiaTheme
import kotlinx.coroutines.runBlocking

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            Config.init(PrefDataStore.get(baseContext)) {
                if (isInstanceUrlInitialized()) {
                    val connected = runBlocking {
                        Http.testConnection(Config.run?.instance)
                    }
                    if(connected) {
                        Service.init()
                        if(isTokenPresent()) displayContent()
                        else finish()
                    } else finish()
                } else {
                    finish()
                }
            }
        }

    }

    private fun displayContent() {
        setContent {
            MiaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LogPage{ finish() }
                }
            }
        }
    }
}