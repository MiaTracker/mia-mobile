package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Tag(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String
)
