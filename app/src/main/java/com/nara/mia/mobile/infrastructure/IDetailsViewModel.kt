package com.nara.mia.mobile.infrastructure

import androidx.navigation.NavController

interface IDetailsViewModel {
    fun refresh(callback: () -> Unit)

    fun delete(navController: NavController)
}
