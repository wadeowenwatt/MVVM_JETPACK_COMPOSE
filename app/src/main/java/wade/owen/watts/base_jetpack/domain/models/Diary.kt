package wade.owen.watts.base_jetpack.domain.models

import java.util.Date

data class Diary(
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdDate: Date,
    val updatedDate: Date,
)
