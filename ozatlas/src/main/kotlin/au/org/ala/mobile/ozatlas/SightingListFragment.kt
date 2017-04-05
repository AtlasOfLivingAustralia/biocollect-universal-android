package au.org.ala.mobile.ozatlas

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import au.org.ala.mobile.ozatlas.biocollect.SightingProvider
import au.org.ala.mobile.ozatlas.db.SightingDao
import kotlinx.android.synthetic.main.fragment_sighting_list.*
import timber.log.Timber
import kotlinx.android.synthetic.main.fragment_sighting_list.view.*

class SightingListFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null

    companion object {
        const val SELECTED_KEY = "selected_position"
        const val ARG_COLUMN_COUNT = "column-count"
        const val SIGHTING_LOADER = 50
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onSightingSelected(sightingUri: Uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT, mColumnCount)
        }
    }

    lateinit var sightingDao : SightingDao
    lateinit var mSightingAdapater : SightingAdapter
    lateinit var mRecyclerView : RecyclerView
    var mPosition = RecyclerView.NO_POSITION

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView")
        val view = inflater.inflate(R.layout.fragment_sighting_list, container, false)

        sightingDao = OzAtlasApp[context].component.daoSession().sightingDao
        mSightingAdapater = SightingAdapter(null, sightingDao) { v, s, i ->
            Timber.d("Sighting clicked")
            val uri = SightingProvider.CONTENT_URI.buildUpon().appendPath(s.id.toString()).build()
            val a = mListener
            if (a is OnListFragmentInteractionListener) {
                a.onSightingSelected(uri)
            } else {
                Timber.e("$a does not implement callback interface!")
            }
            mPosition = i
        }

        val recycler = view.findViewById(R.id.sighting_list_view)

        // Set the adapter
        if (recycler is RecyclerView) {

            val context = view.getContext()
            if (mColumnCount <= 1) {
                recycler.layoutManager = LinearLayoutManager(context)
            } else {
                recycler.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            recycler.setHasFixedSize(true)
            recycler.adapter = mSightingAdapater
            mRecyclerView = recycler
        } else {
            Timber.e("View is not a recycler view!")
        }

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY)
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("onAttach")

        val parent = parentFragment ?: context
        if (parent is OnListFragmentInteractionListener) {
            mListener = parent
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d("onDetach")
        mListener = null
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Timber.d("onSaveInstanceState")
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != RecyclerView.NO_POSITION) {
            outState?.putInt(SELECTED_KEY, mPosition)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Timber.d("onActivityCreated")
        loaderManager.initLoader<Cursor>(SIGHTING_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Timber.d("onCreateLoader")
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
//        val sortOrder = SightingDao.Properties.ServerLastUpdated + " ASC"

//        val locationSetting = Utility.getPreferredLocation(activity)
//        val weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis())

        return CursorLoader(activity,
                SightingProvider.CONTENT_URI,
                null,
                null,
                null,
                null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        Timber.d("onLoadFinished")
        Timber.d("Data count: ${data?.count}")
        mSightingAdapater.changeCursor(data)
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        Timber.d("onLoaderReset")
        mSightingAdapater.changeCursor(null)
    }
}