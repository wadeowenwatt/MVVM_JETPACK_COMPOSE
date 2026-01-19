package wade.owen.watts.base_jetpack.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wade.owen.watts.base_jetpack.data.local.room_db.DiaryDao
import wade.owen.watts.base_jetpack.data.mapper.toDomain
import wade.owen.watts.base_jetpack.data.mapper.toEntity
import wade.owen.watts.base_jetpack.domain.entities.Diary
import wade.owen.watts.base_jetpack.domain.repository.DiaryRepository
import java.util.Date

class DiaryRepositoryImpl(
    private val diaryDao: DiaryDao
) : DiaryRepository {

    override fun getDiaries(
        limit: Int, offset: Int
    ): Flow<List<Diary>> {
        return diaryDao.getDiaries(limit, offset).map { entities ->
            entities.map {
                it.toDomain()
            }
        }
    }

    override fun getDiaryByDate(createdDate: Date): Flow<Diary> {
        val timestamp = (createdDate.time / 1000).toInt()
        return diaryDao.getDiaryByDate(timestamp).map {
            it.toDomain()
        }
    }

    override fun getDiaryById(id: Int): Flow<Diary> {
        return diaryDao.getDiaryById(id).map { it.toDomain() }
    }

    override suspend fun insertDiary(diary: Diary) {
        diaryDao.insertDiary(diary.toEntity())
    }

    override suspend fun updateDiary(diary: Diary) {
        diaryDao.updateDiary(diary.toEntity())
    }

    override suspend fun deleteDiary(diary: Diary) {
        diaryDao.deleteDiary(diary.toEntity())
    }
}