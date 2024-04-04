package com.nara.mia.mobile.models

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.nara.mia.mobile.infrastructure.Config
import java.util.Vector
import android.util.Log

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ImagesConfiguration(
    @JsonProperty("base_url") val baseUrl: String,
    @JsonProperty("secure_base_url") val secureBaseUrl: String,
    @JsonProperty("backdrop_sizes") private val _backdropSizesBS: Vector<String>,
    @JsonProperty("logo_sizes") private val _logoSizesBS: Vector<String>,
    @JsonProperty("poster_sizes") private val _posterSizesBS: Vector<String>,
    @JsonProperty("profile_sizes") private val _profileSizesBS: Vector<String>,
    @JsonProperty("still_sizes") private val _stillSizesBS: Vector<String>,
    ) {

    val backdropSizes: HashMap<Int, String> = toSizesHashMap(_backdropSizesBS)
    val posterSizes: HashMap<Int, String> = toSizesHashMap(_posterSizesBS)

    private fun toSizesHashMap(sizes: Vector<String>): HashMap<Int, String> {
        val map = HashMap<Int, String>()
        sizes.forEach {
            if(it == "original")
                map[Int.MAX_VALUE] = it
            else {
                val num = it.removePrefix("w").toIntOrNull()
                if(num != null) {
                    map[num] = it
                } else {
                    Log.d(Config.const.loggingTag, "Received unexpected image size: $it")
                }
            }
        }
        return map
    }
}
