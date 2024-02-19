package com.nara.mia.mobile.services

import com.nara.mia.mobile.infrastructure.isInstanceUrlInitialized

object Service {
    private var initialized = false

    lateinit var users: Users
    lateinit var media: Media
    lateinit var movies: Movies
    lateinit var series: Series
    lateinit var logset: Logset
    lateinit var watchlist: Watchlist

    fun init() {
        if(initialized || !isInstanceUrlInitialized()) return

        users = ServiceFactory.create(Users::class)
        media = ServiceFactory.create(Media::class)
        movies = ServiceFactory.create(Movies::class)
        series = ServiceFactory.create(Series::class)
        logset = ServiceFactory.create(Logset::class)
        watchlist = ServiceFactory.create(Watchlist::class)

        initialized = true
    }
}