package wade.owen.watts.base_jetpack.domain.repository

import kotlinx.coroutines.flow.Flow
import wade.owen.watts.base_jetpack.domain.entities.Diary
import java.util.Date

interface DiaryRepository {
    fun getDiaries(limit: Int, offset: Int): Flow<List<Diary>>

    fun getDiaryByDate(createdDate: Date): Flow<Diary>

    fun getDiaryById(id: Int): Flow<Diary>

    suspend fun insertDiary(diary: Diary)

    suspend fun updateDiary(diary: Diary)

    suspend fun deleteDiary(diary: Diary)
}