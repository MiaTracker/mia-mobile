package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class UserToken(
    @JsonProperty("token") val token: String,
    @JsonProperty("expiry_date") val expiryDate: Date
)
