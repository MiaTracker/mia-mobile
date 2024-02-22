package com.nara.mia.mobile.infrastructure

import androidx.navigation.NavController
import com.nara.mia.mobile.models.Log
import com.nara.mia.mobile.models.Source

interface IDetailsViewModel {
    fun refresh(callback: () -> Unit)

    fun delete(navController: NavController)

    fun deleteSource(source: Int)

    fun deleteLog(log: Int)

    fun isSourceValid(source: Source): Boolean

    fun saveSource(source: Source, callback: () -> Unit)

    fun isLogValid(log: Log): Boolean

    fun saveLog(log: Log, callback: () -> Unit)
}
