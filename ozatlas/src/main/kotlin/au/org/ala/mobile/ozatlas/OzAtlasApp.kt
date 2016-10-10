package au.org.ala.mobile.ozatlas

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
import timber.log.Timber.DebugTree

class OzAtlasApp : Application() {

//    val component : OzAtlasComponent by lazy { OzAtlasComponent.Initializer.init(this) }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        LeakCanary.install(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            // TODO Crashlytics.start(this)
            // TODO Timber.plant(CrashlyticsTree())
        }

        buildComponentAndInject()
    }

    fun buildComponentAndInject() {

    }
}