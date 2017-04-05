package au.org.ala.mobile.ozatlas

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import au.org.ala.mobile.ozatlas.dagger.ApplicationScope
import au.org.ala.mobile.ozatlas.util.MoshiTypeAdapters
import au.org.ala.mobile.ozatlas.util.IntentFactory
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

const val DISK_CACHE_SIZE = 50L * 1024L * 1024L // 50MB

@Module(includes = arrayOf(ApiModule::class))
class DataModule {

    companion object {
        fun createOkHttpClient(app : Application) : OkHttpClient.Builder {
            val cacheDir = File(app.cacheDir, "http")
            val cache = Cache(cacheDir, DISK_CACHE_SIZE)
            return OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .cache(cache)
        }
    }

    @Provides
    @ApplicationScope
    fun provideSharedPreferences(app : Application) = app.getSharedPreferences("ozatlas", Context.MODE_PRIVATE)

    @Provides
    @ApplicationScope
    fun provideRxSharedPreferences(preferences: SharedPreferences) = RxSharedPreferences.create(preferences)

    @Provides
    @ApplicationScope
    fun provideIntentFactory() = IntentFactory.REAL

    @Provides
    @ApplicationScope
    fun provideMoshi() = Moshi.Builder().add(MoshiTypeAdapters()).build()
}
