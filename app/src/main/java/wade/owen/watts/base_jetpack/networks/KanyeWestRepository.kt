package wade.owen.watts.base_jetpack.networks

interface IKanyeWestRepository {
    suspend fun getRandomQuote(): String
}

class KanyeWestRepository : IKanyeWestRepository {
    override suspend fun getRandomQuote(): String {
        return ""
    }
}