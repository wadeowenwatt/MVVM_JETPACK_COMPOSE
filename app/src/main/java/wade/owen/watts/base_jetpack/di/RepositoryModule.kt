package wade.owen.watts.base_jetpack.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wade.owen.watts.base_jetpack.data.local.room_db.DiaryDao
import wade.owen.watts.base_jetpack.data.remote.ApiService
import wade.owen.watts.base_jetpack.data.repository.DiaryRepositoryImpl
import wade.owen.watts.base_jetpack.data.repository.RandomQuoteRepositoryImpl
import wade.owen.watts.base_jetpack.domain.repository.DiaryRepository
import wade.owen.watts.base_jetpack.domain.repository.RandomQuoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRandomQuoteRepository(
        apiService: ApiService
    ): RandomQuoteRepository {
        return RandomQuoteRepositoryImpl(
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