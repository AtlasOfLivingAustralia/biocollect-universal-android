package au.org.ala.mobile.ozatlas

import okhttp3.Interceptor
import okhttp3.Response

class AlaTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.header("userName", "")
        builder.header("apiKey", "")
        return chain.proceed(builder.build())
    }
}