package au.org.ala.mobile.ozatlas.biocollect

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import au.org.ala.mobile.ozatlas.BuildConfig
import au.org.ala.mobile.ozatlas.R
import au.org.ala.mobile.ozatlas.biocollect.dto.Output
import au.org.ala.mobile.ozatlas.biocollect.upload.Upload
import au.org.ala.mobile.ozatlas.db.*
import au.org.ala.mobile.ozatlas.db.SightingDao.Properties.*
import au.org.ala.mobile.ozatlas.util.toMap
import com.squareup.moshi.JsonDataException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

class SightingSyncAdapter(private val client : BioCollectClient, private val daoMaster: DaoMaster, context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context,autoInitialize) {

    val accountManager = AccountManager.get(context)

    override fun onPerformSync(account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult) {
        val username = account.name
        Timber.d("Getting Auth Token for Sync")
        val authKey = accountManager.blockingGetAuthToken(account, account.type, false)
        Timber.d("Got Auth Token $authKey for $username")

        val projectId = PreferenceManager.getDefaultSharedPreferences(context).getString("projectId", BuildConfig.PROJECT_ID)
        val projectActivityId = PreferenceManager.getDefaultSharedPreferences(context).getString("projectActivityId", BuildConfig.PROJECT_ACTIVITY_ID)

        Timber.d("Getting records for $projectActivityId for $username")
        val recordsCall = client.records(username, authKey, projectActivityId)
        Timber.d("Got records for $projectActivityId for $username")
        try {
            val recordsResponse = recordsCall.execute()
            Timber.d("Got records for $projectActivityId for $username")
            if (recordsResponse.isSuccessful) {
                val records = recordsResponse.body()
                Timber.d("Got ${records.records.size} records from web service for $username")
                val wsRecords = records.records.toMap{ it.outputId to it }
                val wsOutputs = records.outputs.toMap{ it.outputId to it }

                val session = daoMaster.newSession()
                val sightingDao = session.sightingDao
                // TODO Maybe don't load the whole db table into memory?

                val insertRecords = emptyList<Long>()
                val updateRecords = emptyList<Long>()
                val deleteRecords = emptyList<Long>()

                session.callInTx {
                    Timber.d("Getting existing db records")
                    val dbRecords = sightingDao.queryBuilder().where(Uuid.`in`(wsRecords.keys), AccountName.eq(username)).list().toMap { it.uuid to it }
                    Timber.d("Got ${dbRecords.size} records from db for $username")

                    val inserts = wsRecords.keys.minus(dbRecords.keys)
                    Timber.d("Inserting $inserts into db for $username")
                    val maybeUpdates = wsRecords.keys.intersect(dbRecords.keys)
                    Timber.d("Maybe updates $maybeUpdates")
                    // TODO !(dbRecords[it]?.updatedLocally ?: false) -> conflict
                    val deletes = dbRecords.filter { !it.value.updatedLocally }.keys.minus(wsRecords.keys)
//                    records.records.forEach { it.status }
                    Timber.d("Deleting $inserts from db for $username")

                    // TODO check missing outputs
                    // do inserts
                    sightingDao.insertInTx(inserts.map { wsRecords[it]!! }.mapNotNull { wsOutputs[it.outputId]?.let { o -> it.asDbRecord(o).apply { accountName = account.name } } })
                    // do deletes
                    sightingDao.deleteInTx(dbRecords.filterKeys { deletes.contains(it) }.values)
                    // do updates of records that aren't updated locally
                    // TODO dbr.updatedLocally && !dbr.fieldsEqual(it) -> conflict
                    val updateRecords = maybeUpdates
                            .map { wsRecords[it]!! }
                            .mapNotNull { wsRecord -> wsOutputs[wsRecord.outputId]?.let { o -> wsRecord.asDbRecord(o) } }
                            .filter { dbRecords[it.uuid]?.let { dbr -> !dbr.updatedLocally && !dbr.fieldsEqual(it) } ?: false }
                            .mapNotNull { dbRecords[it.uuid]?.updateFrom(it) }
                    sightingDao.updateInTx(updateRecords)

                    wsOutputs.forEach {
                        syncPhotos(account.name, it.key ?: "", it.value, session)
                    }

                    val sendToServerQuery = sightingDao.queryBuilder().where(UpdatedLocally.eq(true), AccountName.eq(username)).build()


                    sendToServerQuery.listLazy().forEach { sighting ->
                        Timber.d("TODO Upload ${sighting.uuid} to web for $username")
//                        Upload(projectId = projectId)
                    }
                }

                //context.contentResolver.notifyChange( , null)
            } else {
                val responseCode = recordsResponse.code()
                if (responseCode in setOf(401,403)) {
                    syncResult.stats.numAuthExceptions += 1
                    accountManager.invalidateAuthToken(account.type, authKey)
                } else {
                    Timber.e("Response code $responseCode while calling BioCollect listRecordsForProjectActivityAndUser, body text: ${recordsResponse.errorBody().string()}")
                    syncResult.stats.numIoExceptions += 1
                }
            }
        } catch (e: IOException) {
            Timber.e(e, "IO Exception calling Biocache Sightings records service")
            syncResult.stats.numIoExceptions += 1
        } catch (e: JsonDataException) {
            Timber.e(e, "JSON parse exception")
            syncResult.stats.numParseExceptions += 1
        } catch (e: RuntimeException) {
            Timber.e(e, "Unexpected exception")
            syncResult.stats.numParseExceptions += 1
        }
    }

    private fun syncPhotos(accountName: String, outputId: String, output: Output, session: DaoSession) {
        val photoDao = session.photoDao
        val maybeSighting = session.sightingDao.queryBuilder().where(SightingDao.Properties.Uuid.eq(outputId), SightingDao.Properties.AccountName.eq(accountName)).build().unique()
        maybeSighting?.let { sighting ->
            val sightingPk = sighting.id
            val qb = photoDao.queryBuilder()
            qb.where(PhotoDao.Properties.SightingId.eq(sightingPk))
            val photos = qb.build().list()

            val records = photos.filter { it.synced }.map { it.path }.toSet()
            val data = output.data?.sightingPhoto?.toMap { it.identifier to it } ?: emptyMap()

            val deletes = records.minus(data)
            val adds = data.keys.minus(records)

            // TODO Filter to only this sighting
            photoDao.queryBuilder().where(PhotoDao.Properties.Path.`in`(deletes)).buildDelete().executeDeleteWithoutDetachingEntities()
            val inserts = adds.map { url ->
                Photo().apply {
                    this.sightingId = sightingPk
                    this.path = url
                    this.licence = data[url]?.licence
                    this.attribution = data[url]?.attribution
                    this.synced = true
                }
            }
            photoDao.insertInTx(inserts)
        }

    }

    // TODO Check for cancelled
    private val cancelled: Boolean get() = Thread.currentThread().isInterrupted


    @Throws(IOException::class)
    inline fun <T> retry(f: () -> T) : T {
        var lastException: Exception? = null
        for (x in listOf(1,2,3,5,8,13)) {
            try {
                return f()
            } catch (e: Exception) {
                lastException = e
                when (e) {
                    is SSLHandshakeException, is SocketTimeoutException, is IOException -> { Timber.d("Caught exception, sleeping for $x seconds", e); Thread.sleep(x * 1000L) }
                    else -> { Timber.d("Retry doesn't know how to handle:", e); throw RuntimeException(e) }
                }
            }
        }
        throw IOException("Failed to retry block", lastException)
    }

    companion object {
        val LOG_TAG = SightingSyncAdapter::class.java.simpleName
        // Interval at which to sync with the weather, in seconds.
        // 60 seconds (1 minute) * 180 = 3 hours
        val SYNC_INTERVAL = 60 * 180
        val SYNC_FLEXTIME = SYNC_INTERVAL / 3
        private val DAY_IN_MILLIS = 1000 * 60 * 60 * 24.toLong()
        private val WEATHER_NOTIFICATION_ID = 3004

        /**
         * Helper method to schedule the sync adapter periodic execution
         */
        fun configurePeriodicSync(context: Context, syncInterval: Int, flexTime: Int) {
            val account = getSyncAccount(context)
            val authority = context.getString(R.string.content_authority)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // we can enable inexact timers in our periodic sync
                val request = SyncRequest.Builder().syncPeriodic(syncInterval.toLong(), flexTime.toLong()).setSyncAdapter(account, authority).setExtras(Bundle()).build()
                ContentResolver.requestSync(request)
            } else {
                ContentResolver.addPeriodicSync(account,
                        authority, Bundle(), syncInterval.toLong())
            }
        }

        /**
         * Helper method to have the sync adapter sync immediately
         * @param context The context used to access the account service
         */
        fun syncImmediately(context: Context) {
            val bundle = Bundle()
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.requestSync(getSyncAccount(context),
                    context.getString(R.string.content_authority), bundle)
        }

        /**
         * Helper method to get the fake account to be used with SyncAdapter, or make a new one
         * if the fake account doesn't exist yet.  If we make a new account, we call the
         * onAccountCreated method so we can initialize things.

         * @param context The context used to access the account service
         * *
         * @return a fake account.
         */
        fun getSyncAccount(context: Context): Account? {
            // Get an instance of the Android account manager
            val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

            // Create the account type and default account
            val accounts = accountManager.getAccountsByType(context.getString(R.string.ala_account_type))

            return if (accounts.size > 0) accounts[0] else null
        }

        fun onAccountCreated(newAccount: Account, context: Context) {
            /*
             * Since we've created an account
             */
            configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME)

            /*
             * Without calling setSyncAutomatically, our periodic sync will not be enabled.
             */
            ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true)

            /*
             * Finally, let's do a sync to get things started
             */
            syncImmediately(context)
        }

        fun initializeSyncAdapter(context: Context) {
            getSyncAccount(context)
        }
    }
}