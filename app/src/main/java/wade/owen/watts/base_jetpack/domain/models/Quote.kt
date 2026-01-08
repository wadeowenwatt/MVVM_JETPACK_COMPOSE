package wade.owen.watts.base_jetpack.domain.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Quote(
    val quote: String
)