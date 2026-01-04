package wade.owen.watts.base_jetpack.data.models.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteEntity(
    val quote: String
)