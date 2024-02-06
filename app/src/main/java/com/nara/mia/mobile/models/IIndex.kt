package com.nara.mia.mobile.models

import com.nara.mia.mobile.enums.MediaType

interface IIndex {
    val type: MediaType
    val posterPath: String?
    val title: String
}