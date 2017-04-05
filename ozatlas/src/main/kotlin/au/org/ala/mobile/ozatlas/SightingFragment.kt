package au.org.ala.mobile.ozatlas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sighting.*

class SightingFragment : Fragment(), SightingListFragment.OnListFragmentInteractionListener {

    companion object {
        const val SIGHTING_DETAIL_FRAGMENT = "SIGHTING_DETAIL_FRAGMENT"
        const val SIGHTING_LIST_FRAGMENT = "SIGHTING_LIST_FRAGMENT"
    }

    var mTwoPane : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sighting, container, false)

        childFragmentManager.beginTransaction().replace(R.id.sighting_list_container, SightingListFragment(), SIGHTING_LIST_FRAGMENT).commit()
        if (sighting_detail_container != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                childFragmentManager.beginTransaction().replace(R.id.sighting_detail_container, SightingDetailFragment(), SIGHTING_DETAIL_FRAGMENT).commit()
            }
        } else {
            mTwoPane = false
//
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onSightingSelected(sightingUri: Uri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            val fragment = SightingDetailFragment.withArguments(sightingUri)

            childFragmentManager.beginTransaction().replace(R.id.sighting_detail_container, fragment, SIGHTING_DETAIL_FRAGMENT).commit()
        } else {
            val intent = Intent(context, SightingDetailActivity::class.java).setData(sightingUri)
            startActivity(intent)
        }

    }
}