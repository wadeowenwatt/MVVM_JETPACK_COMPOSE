package wade.owen.watts.base_jetpack.data.local.room_db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import wade.owen.watts.base_jetpack.data.models.entity.DiaryEntity

@Dao
interface DiaryDao {

    @Query("SELECT * FROM diary ORDER BY created_date DESC LIMIT :limit OFFSET :offset")
    fun getDiaries(limit: Int, offset: Int): List<DiaryEntity>

    @Query("SELECT * FROM diary WHERE created_date = :createdDate")
    fun getDiaryByDate(createdDate: Int): DiaryEntity

    @Insert
    fun insertDiary(diary: DiaryEntity)

    @Delete
    fun deleteDiary(diary: DiaryEntity)
}