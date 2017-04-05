package au.org.ala.mobile.ozatlas

import android.os.Build
import au.org.ala.mobile.ozatlas.dagger.ApplicationScope
import au.org.ala.mobile.ozatlas.biocollect.BioCollectClient
import au.org.ala.mobile.ozatlas.login.AlaMobileLoginClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Named

@Module
class ApiModule {

    companion object {
        const val PRODUCTION_BIOCOLLECT_URL = "https://biocollect.ala.org.au/ws/"
        const val TEST_BIOCOLLECT_URL = BuildConfig.BIOCOLLECT_BASE_URL
        const val PRODUCTION_AUTH_URL = "https://m.ala.org.au/mobileauth/"

        private fun retrofit(baseUrl: HttpUrl, client: OkHttpClient, moshi: Moshi) =
                Retrofit.Builder()
                        .client(client)
                        .baseUrl(baseUrl)
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build()
    }

    @Provides
    @ApplicationScope
    @Named("BioCollectRetrofit")
    fun provideBioCollectRetrofit(@Named("biocollectUrl") baseUrl : HttpUrl, @Named("Api") client: OkHttpClient, moshi: Moshi): Retrofit {
        //Timber.v("Using $baseUrl for BioCollectRetrofit, with ${client.interceptors().size} client interceptors")
        return retrofit(baseUrl, client, moshi)
    }

    @Provides
    @ApplicationScope
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(@Named("AuthUrl") baseUrl : HttpUrl, @Named("Api") client: OkHttpClient, moshi: Moshi): Retrofit {
        //Timber.v("Using $baseUrl for AuthRetrofit, with ${client.interceptors().size} client interceptors")
        return retrofit(baseUrl, client, moshi)
    }

    @Provides
    @ApplicationScope
    @Named("AuthUrl")
    fun provideAuthUrl() = HttpUrl.parse(PRODUCTION_AUTH_URL)

    @Provides
    @ApplicationScope
    fun provideAuthClient(@Named("AuthRetrofit") retrofit: Retrofit) = retrofit.create(AlaMobileLoginClient::class.java)

    @Provides
    @ApplicationScope
    fun provideBioCollectClient(@Named("BioCollectRetrofit") retrofit: Retrofit) = retrofit.create(BioCollectClient::class.java)
}
