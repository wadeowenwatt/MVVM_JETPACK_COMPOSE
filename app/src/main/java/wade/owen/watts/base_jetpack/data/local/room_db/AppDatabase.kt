package wade.owen.watts.base_jetpack.data.local.room_db

import androidx.room.Database
import androidx.room.RoomDatabase
import wade.owen.watts.base_jetpack.data.models.entity.DiaryEntity

@Database(entities = [DiaryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}