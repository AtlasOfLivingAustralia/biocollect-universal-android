package au.org.ala.mobile.ozatlas.util

import android.content.Intent
import android.net.Uri

/** Creates [Intent]s for launching into external applications.  */
interface IntentFactory {
    fun createUrlIntent(url: String): Intent

    companion object {
        val REAL = object : IntentFactory {
            override fun createUrlIntent(url: String) =
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
        }
    }
}