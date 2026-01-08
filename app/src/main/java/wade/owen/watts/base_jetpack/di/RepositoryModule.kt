package wade.owen.watts.base_jetpack.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wade.owen.watts.base_jetpack.data.local.room_db.DiaryDao
import wade.owen.watts.base_jetpack.data.remote.ApiService
import wade.owen.watts.base_jetpack.data.repository.DiaryRepository
import wade.owen.watts.base_jetpack.data.repository.DiaryRepositoryImpl
import wade.owen.watts.base_jetpack.data.repository.KanyeWestRepository
import wade.owen.watts.base_jetpack.data.repository.KanyeWestRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideKanyeWestRepository(
        apiService: ApiService
    ): KanyeWestRepository {
        return KanyeWestRepositoryImpl(
            apiService
        )
    }

    @Provides
    @Singleton
    fun provideDiaryRepository(
        diaryDao: DiaryDao
    ): DiaryRepository {
        return DiaryRepositoryImpl(
            diaryDao
        )
    }
}