package wade.owen.watts.base_jetpack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import wade.owen.watts.base_jetpack.data.models.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}