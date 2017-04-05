package au.org.ala.mobile.ozatlas

import au.org.ala.mobile.ozatlas.dagger.ApplicationScope
import au.org.ala.mobile.ozatlas.db.DaoMaster
import au.org.ala.mobile.ozatlas.db.DaoSession
import dagger.Component
import dagger.Provides
import org.greenrobot.greendao.database.Database
import javax.inject.Singleton

/**
 * The core component for the ozAtlas application
 */
@ApplicationScope
@Component(modules = arrayOf(OzAtlasAppModule::class, DebugDataModule::class, DbModule::class))
interface OzAtlasComponent : OzAtlasGraph {

    /**
     * An initializer that creates the graph from an application.
     */
    object Initializer {
        internal fun init(app: OzAtlasApp): OzAtlasComponent =
            DaggerOzAtlasComponent.builder().ozAtlasAppModule(OzAtlasAppModule(app)).build()
    }// No instances.

}
