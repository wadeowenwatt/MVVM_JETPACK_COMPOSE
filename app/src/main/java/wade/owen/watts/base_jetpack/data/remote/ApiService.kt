package wade.owen.watts.base_jetpack.data.remote

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import wade.owen.watts.base_jetpack.domain.models.Quote

interface ApiService {
    @GET("/")
    suspend fun getRandomQuote(): ApiResponse<Quote>
}