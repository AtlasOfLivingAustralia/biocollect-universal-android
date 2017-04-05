package au.org.ala.mobile.ozatlas

import au.org.ala.mobile.ozatlas.dagger.ApiEndpoint
import au.org.ala.mobile.ozatlas.dagger.ApplicationScope
import au.org.ala.mobile.ozatlas.util.CurlLoggingInterceptor
import com.f2prateek.rx.preferences.Preference
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Named

@Module(includes = arrayOf(ApiModule::class))
class DebugApiModule {
    @Provides
    @ApplicationScope
    @Named("biocollectUrl")
    fun provideBiocollectUrl(@ApiEndpoint apiEndpoint: Preference<String>) = HttpUrl.parse(apiEndpoint.get())

    @Provides
    @ApplicationScope
    fun provideCurlLoggingInterceptor() = CurlLoggingInterceptor( HttpLoggingInterceptor.Logger { message : String -> Timber.tag("Curl").v(message) } )

    @Provides
    @ApplicationScope
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor() { message ->
            Timber.tag("OkHttp").v(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

    @Provides
    @ApplicationScope
    @Named("acceptJson")
    fun provideApplicationJsonHeaderInterceptor() =
        Interceptor() { chain ->
            chain.proceed(chain.request().newBuilder().addHeader("Accept", "application/json").build())
        }

    @Provides
    @ApplicationScope
    @Named("Api")
    fun provideApiClient(@Named("base") client: OkHttpClient,
                         loggingInterceptor: HttpLoggingInterceptor,
                         curlLoggingInterceptor: CurlLoggingInterceptor) =
            client.newBuilder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(curlLoggingInterceptor)
                    .build()
}

