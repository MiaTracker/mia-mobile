package com.nara.mia.mobile.enums

import com.fasterxml.jackson.annotation.JsonProperty

enum class SourceType {
    @JsonProperty("torrent")
    Torrent,
    @JsonProperty("web")
    Web,
    @JsonProperty("jellyfin")
    Jellyfin
}
