package wade.owen.watts.base_jetpack.data.mapper

import wade.owen.watts.base_jetpack.data.models.entity.DiaryEntity
import wade.owen.watts.base_jetpack.domain.entities.Diary
import java.util.Date

fun DiaryEntity.toDomain(): Diary {
    return Diary(
        id = id,
        title = title,
        content = content,
        createdDate = Date(createdDate.toLong() * 1000),
        updatedDate = Date(updatedDate.toLong() * 1000)
    )
}

fun Diary.toEntity(): DiaryEntity {
    return DiaryEntity(
        id = id,
        title = title,
        content = content,
        createdDate = (createdDate.time / 1000).toInt(),
        updatedDate = (updatedDate.time / 1000).toInt()
    )
}
