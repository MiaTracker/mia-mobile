package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty

data class AlternativeTitle(
    @JsonProperty("id") val id: Int,
    @JsonProperty("title") val title: String
)
