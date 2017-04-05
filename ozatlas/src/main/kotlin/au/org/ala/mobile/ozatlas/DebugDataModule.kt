package au.org.ala.mobile.ozatlas

import android.app.Application
import android.content.SharedPreferences
import au.org.ala.mobile.ozatlas.dagger.ApiEndpoint
import au.org.ala.mobile.ozatlas.dagger.ApplicationScope
import com.f2prateek.rx.preferences.Preference
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.inject.Named
import javax.inject.Qualifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.annotation.AnnotationRetention.RUNTIME

@Module(includes = arrayOf(DataModule::class, DebugApiModule::class))
class DebugDataModule {

    // TODO HTTPS socket factory and proxy address for debugging
    @Provides
    @ApplicationScope
    @Named("base")
    fun provideOkHttpClient(app: Application) =
        DataModule.createOkHttpClient(app).build()

    @Provides
    @ApplicationScope
    @ApiEndpoint
    fun provideEndpointPreference(prefs: RxSharedPreferences): Preference<String> {
        return prefs.getString("debug_endpoint", ApiEndpoints.TEST.url)
    }

    @Provides
    @ApplicationScope
    @IsMockMode
    fun provideIsMockMode(@ApiEndpoint endpoint: Preference<String>,
                          @IsInstrumentationTest isInstrumentationTest: Boolean): Boolean {
        // Running in an instrumentation forces mock mode.
        return isInstrumentationTest || ApiEndpoints.isMockMode(endpoint.get()!!)
    }

    @Provides
    @ApplicationScope
    fun providePicasso(@Named("base") client: OkHttpClient, @IsMockMode isMockMode: Boolean, app: Application): Picasso {
        val builder = Picasso.Builder(app).downloader(OkHttp3Downloader(client))
        if (isMockMode) {
//            builder.addRequestHandler(MockRequestHandler(behavior, app.assets))
        }
        builder.listener { picasso, uri, exception -> Timber.e(exception, "Error while loading image %s", uri) }
        return builder.build()
    }

    private fun createBadSslSocketFactory(): SSLSocketFactory {
        try {
            // Construct SSLSocketFactory that accepts any cert.
            val context = SSLContext.getInstance("TLS")
            val permissive = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
            }
            context.init(null, arrayOf<TrustManager>(permissive), null)
            return context.socketFactory
        } catch (e: Exception) {
            throw AssertionError(e)
        }

    }
}

@Qualifier
@Retention(RUNTIME)
annotation class IsInstrumentationTest

@Qualifier
@Retention(RUNTIME)
annotation class IsMockMode
