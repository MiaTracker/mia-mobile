package com.nara.mia.mobile.enums

import com.fasterxml.jackson.annotation.JsonProperty

enum class MediaType {
    @JsonProperty("movie") Movie,
    @JsonProperty("series") Series
}