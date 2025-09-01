package wade.owen.watts.base_jetpack.data.repository

interface KanyeWestRepository {
    suspend fun getRandomQuote(): String
}

class KanyeWestRepositoryImpl : KanyeWestRepository {
    override suspend fun getRandomQuote(): String {
        return ""
    }
}