package wade.owen.watts.base_jetpack.domain.entities

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Quote(
    val quote: String
)