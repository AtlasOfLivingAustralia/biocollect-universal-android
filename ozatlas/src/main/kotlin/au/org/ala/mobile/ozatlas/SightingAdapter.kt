package au.org.ala.mobile.ozatlas

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import au.org.ala.mobile.ozatlas.db.Sighting
import au.org.ala.mobile.ozatlas.db.SightingDao
import au.org.ala.mobile.ozatlas.ui.CursorRecyclerViewAdapter
import timber.log.Timber

class SightingAdapter(cursor: Cursor?, val sightingDao: SightingDao, var onClickListener: (v: View, s: Sighting, i: Int) -> Unit) : CursorRecyclerViewAdapter<SightingAdapter.ViewHolder>(cursor, SightingDao.Properties.Uuid.columnName) {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val speciesTextView = view.findViewById(R.id.list_item_species_name) as TextView
        val uuidTextView = view.findViewById(R.id.list_item_uuid) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.d("onCreateViewHolder")
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_sighting, parent, false)
        // set the view's size, margins, paddings and layout parameters

        val vh = ViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, cursor: Cursor, position: Int) {
        Timber.d("onBindViewHolder, pos: $position")
        val sighting = sightingDao.readEntity(cursor, 0)
        Timber.d("Loaded sighting: ${sighting.uuid} ${sighting.speciesName}")
        viewHolder.speciesTextView.text = sighting.speciesName
        viewHolder.uuidTextView.text = sighting.uuid
        //viewHolder.view.setOnClickListener { onClickListener(it, sighting, position) }
    }

}
