package com.nara.mia.mobile.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nara.mia.mobile.models.ImagesConfiguration
import com.nara.mia.mobile.models.UserToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object Config {
    val configChanged = Signal()

    var run: RunConfig? = null
    var images: ImagesConfiguration? = null
    val const: ConstConfig = ConstConfig()

    suspend fun init(dataStore: DataStore<Preferences>, callback: () -> Unit) {
        if(run != null) callback()
        run = RunConfig.load(dataStore)
        configChanged += callback
        callback()
    }
}

class RunConfig private constructor(private val dataStore: DataStore<Preferences>) {

    var token: String? = null
        private set
    var tokenExpiryDate: Date? = null
        private set
    var instance: String? = null
        private set

    suspend fun setInstance(url: String) {
        dataStore.edit { preferences ->
            preferences[instanceKey] = url
        }
        instance = url
        Config.configChanged()
    }

    suspend fun setToken(token: UserToken) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token.token
            preferences[tokenExpiryDateKey] = dateFormat.format(token.expiryDate)
        }
        this.token = token.token
        this.tokenExpiryDate = token.expiryDate
        Config.configChanged()
    }

    fun validateToken(): Boolean {
        if(tokenExpiryDate == null || tokenExpiryDate!! < Date()) {
            token = null
            tokenExpiryDate = null
            return false
        }
        return true
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(tokenExpiryDateKey)
        }
        this.token = null
        this.tokenExpiryDate = null
        Config.configChanged()
    }

    companion object {
        private val dateFormat = SimpleDateFormat("dd-MM-yyy", Locale.US)

        private val tokenKey = stringPreferencesKey("token")
        private val tokenExpiryDateKey = stringPreferencesKey("token_expiry_date")
        private val instanceKey = stringPreferencesKey("instance")

        suspend fun load(dataStore: DataStore<Preferences>): RunConfig {
            val conf = RunConfig(dataStore)
            val preferences = runBlocking { dataStore.data.first() }
            conf.token = preferences[tokenKey]
            val res = preferences[tokenExpiryDateKey]
            if(res != null) { conf.tokenExpiryDate = dateFormat.parse(res) }
            conf.instance = preferences[instanceKey]
            conf.validateToken()
            return conf
        }
    }
}

class ConstConfig {
    val loggingTag = "Mia"
}