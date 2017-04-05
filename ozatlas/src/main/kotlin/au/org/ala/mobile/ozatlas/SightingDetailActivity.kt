package au.org.ala.mobile.ozatlas

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SightingDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sightings_detail)

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            val fragment = SightingDetailFragment.withArguments(intent.data)

            supportFragmentManager.beginTransaction().add(R.id.sighting_detail_container, fragment).commit()
        }
    }
}