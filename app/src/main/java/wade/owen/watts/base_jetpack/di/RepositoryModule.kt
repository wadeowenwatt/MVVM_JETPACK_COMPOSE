package wade.owen.watts.base_jetpack.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wade.owen.watts.base_jetpack.data.remote.ApiService
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

}