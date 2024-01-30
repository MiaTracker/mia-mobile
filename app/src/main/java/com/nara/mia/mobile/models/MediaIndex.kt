package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.nara.mia.mobile.enums.MediaType

class MediaIndex(
    @JsonProperty("id") val id: Int,
    @JsonProperty("type") val type: MediaType,
    @JsonProperty("poster_path") val posterPath: String,
    @JsonProperty("stars") val stars: Float,
    @JsonProperty("title") val title: String
)