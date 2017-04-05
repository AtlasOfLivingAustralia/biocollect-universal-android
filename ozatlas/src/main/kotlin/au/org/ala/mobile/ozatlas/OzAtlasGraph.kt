package au.org.ala.mobile.ozatlas

import au.org.ala.mobile.ozatlas.biocollect.BioCollectClient
import au.org.ala.mobile.ozatlas.biocollect.SightingSyncService
import au.org.ala.mobile.ozatlas.db.DaoMaster
import au.org.ala.mobile.ozatlas.db.DaoSession
import au.org.ala.mobile.ozatlas.login.AlaAuthenticator
import au.org.ala.mobile.ozatlas.login.AlaMobileLoginClient
import com.squareup.picasso.Picasso

interface OzAtlasGraph {
    fun inject(app: OzAtlasApp)
    fun inject(authenticator: AlaAuthenticator)
    fun inject(detailFragment: SightingDetailFragment)
    fun inject(sightingSyncService: SightingSyncService)
    fun picasso(): Picasso
    fun biocollectClient() : BioCollectClient
    fun alaMobileLoginClient() : AlaMobileLoginClient
    fun daoSession() : DaoSession
    fun openHelper() : DaoMaster.OpenHelper
}