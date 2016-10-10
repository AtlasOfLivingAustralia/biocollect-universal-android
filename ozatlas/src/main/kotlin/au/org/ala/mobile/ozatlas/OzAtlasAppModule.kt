package au.org.ala.mobile.ozatlas

import dagger.Module
import dagger.Provides
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

@Scope
@Retention(RUNTIME)
annotation class ApplicationScope

@Module class OzAtlasAppModule(private val app: OzAtlasApp) {

    @Provides
    @ApplicationScope
    fun provideApplication() = app
}