package wade.owen.watts.base_jetpack.data.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import wade.owen.watts.base_jetpack.data.models.entity.QuoteEntity
import wade.owen.watts.base_jetpack.data.remote.ApiService
import javax.inject.Inject

interface KanyeWestRepository {
    suspend fun getRandomQuote(
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ): Flow<QuoteEntity>
}

class KanyeWestRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : KanyeWestRepository {
    override suspend fun getRandomQuote(
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ): Flow<QuoteEntity> {
        return flow {
            val quoteRandom = apiService.getRandomQuote()
            quoteRandom.suspendOnSuccess {
                emit(data)
            }.onError {
                onError(message())
            }.onFailure {
                onError(message())
            }
        }.onStart { onStart() }.onCompletion { onComplete() }
    }
}