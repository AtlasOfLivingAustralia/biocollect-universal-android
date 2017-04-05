package au.org.ala.mobile.ozatlas

import au.org.ala.mobile.ozatlas.dagger.ApplicationScope
import au.org.ala.mobile.ozatlas.db.DaoMaster
import au.org.ala.mobile.ozatlas.db.DaoSession
import dagger.Module
import dagger.Provides
import org.greenrobot.greendao.database.Database
import javax.inject.Singleton


@Module class DbModule(private val app: OzAtlasApp) {

    @ApplicationScope @Provides fun openHelper(): DaoMaster.OpenHelper = DaoMaster.DevOpenHelper(app, "ozatlas-db")
    @ApplicationScope @Provides fun database(openHelper: DaoMaster.OpenHelper): Database = openHelper.writableDb
    @ApplicationScope @Provides fun daoMaster(database: Database): DaoMaster = DaoMaster(database)
    @ApplicationScope @Provides fun daoSession(daoMaster: DaoMaster): DaoSession = daoMaster.newSession()
}