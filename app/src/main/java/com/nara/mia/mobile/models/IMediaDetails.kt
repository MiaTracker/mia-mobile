package com.nara.mia.mobile.models

import java.util.Vector

interface IMediaDetails {
    val id: Int
    val posterPath: String?
    val backdropPath: String?
    val stars: Float?
    val title: String
    val alternativeTitles: Vector<AlternativeTitle>
    val status: String?
    val overview: String?
    val tmdbVoteAverage: Float?
    val onWatchlist: Boolean
    val originalLanguage: Language?
    val genres: Vector<Genre>
    val tags: Vector<Tag>
    val sources: Vector<Source>
    val logs: Vector<Log>
}