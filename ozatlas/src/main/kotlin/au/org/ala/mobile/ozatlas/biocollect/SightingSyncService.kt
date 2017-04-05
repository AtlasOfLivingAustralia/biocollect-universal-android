package au.org.ala.mobile.ozatlas.biocollect

import android.app.Service
import android.content.*
import android.os.IBinder
import android.util.Log
import au.org.ala.mobile.ozatlas.OzAtlasApp
import au.org.ala.mobile.ozatlas.db.DaoMaster
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class SightingSyncService : Service() {
    @Inject
    lateinit var client : BioCollectClient
    @Inject
    lateinit var daoMaster: DaoMaster
    private val sightingSyncAdapter by lazy(NONE) { SightingSyncAdapter(client, daoMaster, this, true) }

    override fun onCreate() {
        Timber.d("onCreate - SightingSyncService")
        OzAtlasApp[applicationContext].component.inject(this)
        val adapter = sightingSyncAdapter // init on create
    }

    override fun onBind(intent: Intent): IBinder = sightingSyncAdapter.syncAdapterBinder
}

