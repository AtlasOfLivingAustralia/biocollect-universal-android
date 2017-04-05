package au.org.ala.mobile.ozatlas

import android.database.Cursor
import android.databinding.ObservableField
import android.databinding.ObservableParcelable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import au.org.ala.mobile.ozatlas.biocollect.SightingProvider
import au.org.ala.mobile.ozatlas.db.*
import timber.log.Timber
import javax.inject.Inject

class SightingDetailFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        val DETAIL_URI = "URI"

        val DETAIL_LOADER = 100
        val PHOTO_LOADER = 200

        fun withArguments(uri: Uri) : SightingDetailFragment =
            SightingDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DETAIL_URI, uri)
                }
            }

//        private val DETAIL_COLUMNS = arrayOf(
//                SightingDao.TABLENAME + "." + SightingDao.Properties.Id.columnName,
//                SightingDao.Properties.Uuid.columnName,
////                SightingDao.Properties.AccountName.columnName,
//                SightingDao.Properties.Accuracy.columnName,
//                SightingDao.Properties.Comments.columnName,
//                SightingDao.Properties.Confident.columnName, WeatherEntry.COLUMN_PRESSURE, WeatherEntry.COLUMN_WIND_SPEED, WeatherEntry.COLUMN_DEGREES, WeatherEntry.COLUMN_WEATHER_ID,
//                // This works because the WeatherProvider returns location data joined with
//                // weather data, even though they're stored in two different tables.
//                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING)
    }

    @Inject
    lateinit var daoSession : DaoSession

    private var mUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        OzAtlasApp[context].component.inject(this)

        val arguments = arguments
        if (arguments != null) {
            mUri = arguments.getParcelable<Uri>(DETAIL_URI)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sighting_detail, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loaderManager.initLoader<Cursor>(DETAIL_LOADER, null, this)
        loaderManager.initLoader<Cursor>(PHOTO_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        Timber.d("onCreateLoader $id")
        return when(id) {
            DETAIL_LOADER -> mUri?.let { CursorLoader(activity, mUri, null, null, null, null) }
            PHOTO_LOADER -> mUri?.let { uri -> CursorLoader(activity, uri.buildUpon().appendPath(SightingProvider.PATH_PHOTO).build(), null, null, null, null) }
            else -> throw IllegalStateException("Loader $id not supported in Sighting Detail Fragment")
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        Timber.d("onLoaderReset")
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        when(loader?.id) {
            DETAIL_LOADER -> loadDetail(loader,data)
            PHOTO_LOADER -> loadPhotos(loader,data)
        }
    }

    private fun loadDetail(loader: Loader<Cursor>?, data: Cursor?) {
        Timber.d("load detail")

        if (data != null && data.moveToFirst()) {
            val sighting = daoSession.sightingDao.readEntity(data, 0)

        }
    }

    private fun loadPhotos(loader: Loader<Cursor>?, data: Cursor?) {
        Timber.d("load photos")
        if (data != null && data.moveToFirst()) {
            val photos = mutableListOf<Photo>()
            while (!data.isAfterLast) {
                photos.add(daoSession.photoDao.readEntity(data, 0))
                data.moveToNext()
            }
        }
    }
}

class SightingDetailViewModel(sighting: Sighting) {
    init {
        sighting.accuracy
        sighting.comments
        sighting.confident
        sighting.date
        sighting.id
        sighting.individualCount
        sighting.latitude
        sighting.localityMatch
        sighting.locationNotes
        sighting.longitude
        sighting.notes
        sighting.recordedBy
        sighting.serverLastUpdated
        sighting.source
        sighting.speciesName
        sighting.tags
        sighting.time
        sighting.uuid
    }

    val accuracy = ObservableField(sighting.accuracy)
    val comments = ObservableField(sighting.comments)
}