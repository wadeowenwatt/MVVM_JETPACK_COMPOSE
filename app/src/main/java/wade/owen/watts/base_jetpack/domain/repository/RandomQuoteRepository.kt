package wade.owen.watts.base_jetpack.domain.repository

import kotlinx.coroutines.flow.Flow
import wade.owen.watts.base_jetpack.domain.entities.Quote

interface RandomQuoteRepository {
    suspend fun getRandomQuote(
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ): Flow<Quote>
}