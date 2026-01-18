package wade.owen.watts.base_jetpack.data.local.room_db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wade.owen.watts.base_jetpack.data.models.entity.DiaryEntity

@Dao
interface DiaryDao {

    @Query("SELECT * FROM diary ORDER BY created_date DESC LIMIT :limit OFFSET :offset")
    fun getDiaries(limit: Int, offset: Int): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary WHERE created_date = :createdDate")
    fun getDiaryByDate(createdDate: Int): Flow<DiaryEntity>

    @Query("SELECT * FROM diary WHERE id = :id")
    fun getDiaryById(id: Int): Flow<DiaryEntity>

    @Insert
    fun insertDiary(diary: DiaryEntity)

    @Delete
    fun deleteDiary(diary: DiaryEntity)

    @Update
    fun updateDiary(diary: DiaryEntity)
}