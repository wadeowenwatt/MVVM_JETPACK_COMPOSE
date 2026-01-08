package wade.owen.watts.base_jetpack.data.repository

import wade.owen.watts.base_jetpack.data.local.room_db.DiaryDao
import wade.owen.watts.base_jetpack.data.mapper.toDomain
import wade.owen.watts.base_jetpack.data.mapper.toEntity
import wade.owen.watts.base_jetpack.domain.models.Diary
import java.util.Date

interface DiaryRepository {
    suspend fun getDiaries(limit: Int, offset: Int): List<Diary>

    suspend fun getDiaryByDate(createdDate: Date): Diary

    suspend fun insertDiary(diary: Diary)

    suspend fun deleteDiary(diary: Diary)
}

class DiaryRepositoryImpl(
    private val diaryDao: DiaryDao
) : DiaryRepository {

    override suspend fun getDiaries(
        limit: Int,
        offset: Int
    ): List<Diary> {
        return diaryDao.getDiaries(limit, offset).map { it.toDomain() }
    }

    override suspend fun getDiaryByDate(createdDate: Date): Diary {
        val timestamp = (createdDate.time / 1000).toInt()
        return diaryDao.getDiaryByDate(timestamp).toDomain()
    }

    override suspend fun insertDiary(diary: Diary) {
        diaryDao.insertDiary(diary.toEntity())
    }

    override suspend fun deleteDiary(diary: Diary) {
        diaryDao.deleteDiary(diary.toEntity())
    }
}