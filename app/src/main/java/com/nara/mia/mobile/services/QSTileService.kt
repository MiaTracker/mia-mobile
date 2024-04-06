package com.nara.mia.mobile.services

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.nara.mia.mobile.LogActivity
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.infrastructure.PrefDataStore
import com.nara.mia.mobile.infrastructure.isInstanceUrlInitialized
import com.nara.mia.mobile.infrastructure.isTokenPresent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class QSTileService : TileService() {
    override fun onTileAdded() {
        super.onTileAdded()
        checkInitialized()
    }

    override fun onClick() {
        val intent = Intent(this, LogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)



        if(Build.VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE)
            startActivityAndCollapse(pendingIntent)
        else startActivity(intent)
        super.onClick()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        Config.configChanged -= { checkInitialized() }
    }

    private fun checkInitialized() {
        runBlocking {
            launch {
                Config.init(PrefDataStore.get(baseContext)) {
                    if (isInstanceUrlInitialized()) {
                        val connected = runBlocking {
                            Http.testConnection(Config.run?.instance)
                        }
                        if(connected) {
                            Service.init()
                            setInitialized(isTokenPresent())
                        } else {
                            setInitialized(false)
                        }
                    } else {
                        setInitialized(false)
                    }
                }
            }
        }
    }

    private fun setInitialized(initialized: Boolean) {
        if(initialized) {
            qsTile.state = Tile.STATE_INACTIVE
            if(Build.VERSION.SDK_INT >= VERSION_CODES.R)
                qsTile.stateDescription = null
        }
        else {
            qsTile.state = Tile.STATE_UNAVAILABLE
            if(Build.VERSION.SDK_INT >= VERSION_CODES.R)
                qsTile.stateDescription = "Application not initialized"
        }
        qsTile.updateTile()
    }
}