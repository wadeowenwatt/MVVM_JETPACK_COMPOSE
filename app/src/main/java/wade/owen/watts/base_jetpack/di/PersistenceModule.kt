package wade.owen.watts.base_jetpack.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wade.owen.watts.base_jetpack.data.local.room_db.AppDatabase
import wade.owen.watts.base_jetpack.data.local.room_db.UserDao
import javax.annotation.Nonnull
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@Nonnull application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            klass = AppDatabase::class.java,
            name = "diary_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
}