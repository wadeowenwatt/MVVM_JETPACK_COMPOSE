package wade.owen.watts.base_jetpack.data.remote

import retrofit2.http.GET
import wade.owen.watts.base_jetpack.data.models.QuoteEntity

interface ApiService {
    @GET()
    suspend fun getRandomQuote(): QuoteEntity
}