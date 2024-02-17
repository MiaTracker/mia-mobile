package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class UserToken(
    @JsonProperty("token") val token: String,
    @JsonProperty("expiry_date") val expiryDate: Date,
    @JsonProperty("admin") val isAdmin: Boolean
)
