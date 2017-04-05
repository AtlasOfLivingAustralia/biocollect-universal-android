package au.org.ala.mobile.ozatlas

import android.app.Application
import android.content.Context
import au.org.ala.mobile.ozatlas.dagger.ApplicationScope
import dagger.Module
import dagger.Provides

@Module class OzAtlasAppModule(private val app: OzAtlasApp) {

    @Provides
    @ApplicationScope
    @IsInstrumentationTest
    fun provideIsInstrumentationTest() : Boolean = false

    @Provides
    @ApplicationScope
    fun provideApplication() : Application = app

    @Provides
    @ApplicationScope
    fun provideContext() : Context = app
}