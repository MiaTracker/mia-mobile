package com.nara.mia.mobile.services

import com.nara.mia.mobile.infrastructure.isInstanceUrlInitialized

object Service {
    private var initialized = false

    lateinit var users: Users
    lateinit var media: Media
    lateinit var movies: Movies
    lateinit var series: Series

    fun init() {
        if(initialized || !isInstanceUrlInitialized()) return

        users = ServiceFactory.create(Users::class)
        media = ServiceFactory.create(Media::class)
        movies = ServiceFactory.create(Movies::class)
        series = ServiceFactory.create(Series::class)

        initialized = true
    }
}