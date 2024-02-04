package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Vector

data class SearchResults(
    @JsonProperty("indexes") val indexes: Vector<MediaIndex>,
    @JsonProperty("external") val external: Vector<ExternalIndex>
)
