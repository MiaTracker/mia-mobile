package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.nara.mia.mobile.enums.SourceType

data class SourceCreate(
    @JsonProperty("name") val name: String,
    @JsonProperty("url") val url: String,
    @JsonProperty("type") val type: SourceType
)
