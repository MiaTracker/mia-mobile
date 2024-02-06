package com.nara.mia.mobile.infrastructure

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class PrefDataStore private constructor() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "config")

    fun get(ctx: Context): DataStore<Preferences> {
        return ctx.dataStore
    }

    companion object {
        private var instance: PrefDataStore? = null

        fun get(ctx: Context): DataStore<Preferences> {
            if(instance == null) instance = PrefDataStore()
            return instance!!.get(ctx)
        }
    }
}