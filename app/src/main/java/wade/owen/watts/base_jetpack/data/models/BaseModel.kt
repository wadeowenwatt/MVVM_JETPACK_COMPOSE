package wade.owen.watts.base_jetpack.data.models

import com.squareup.moshi.JsonClass

/**
 * Just like a base of API response
 */
@JsonClass(generateAdapter = true)
data class BaseModel<T>(
    val status: String,
    val results: List<T>
)
