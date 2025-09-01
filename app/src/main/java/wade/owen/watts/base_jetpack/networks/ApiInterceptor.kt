package wade.owen.watts.base_jetpack.networks

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Todo: Get from secure place
        val token = ""

        // add more query params likes API key,...
        val newUrl = originalUrl.newBuilder()
            .build()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}