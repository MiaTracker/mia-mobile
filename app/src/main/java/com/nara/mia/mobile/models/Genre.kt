package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Genre(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String
)
