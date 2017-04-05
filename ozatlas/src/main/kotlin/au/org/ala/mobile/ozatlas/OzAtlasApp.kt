package au.org.ala.mobile.ozatlas

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
//import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.lang.reflect.Field

class OzAtlasApp : Application() {

    companion object {
        operator fun get(context: Context): OzAtlasApp {
            return context.applicationContext.getRealApplication()
        }
    }

    val component : OzAtlasComponent by lazy { OzAtlasComponent.Initializer.init(this) }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
//        LeakCanary.install(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            // TODO Crashlytics.start(this)
            // TODO Timber.plant(CrashlyticsTree())
        }

        buildComponentAndInject()
    }

    fun buildComponentAndInject() {
        component.inject(this)
    }

}


fun Context.getRealApplication() : OzAtlasApp {
    var application: OzAtlasApp? = null

    val ac = applicationContext

    if (ac is OzAtlasApp) {
        application = ac as OzAtlasApp
    } else {
        var realApplication: Application? = null
        var magicField: Field? = null
        try {
            magicField = ac.javaClass.getDeclaredField("realApplication")
            magicField!!.isAccessible = true
            realApplication = magicField!!.get(ac) as Application
        } catch (e: NoSuchFieldException) {
            Timber.e(e)
        } catch (e: IllegalAccessException) {
            Timber.e(e)
        }

        application = realApplication as OzAtlasApp
    }

    return application
}