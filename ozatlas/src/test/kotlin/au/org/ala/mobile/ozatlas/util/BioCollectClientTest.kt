package au.org.ala.mobile.ozatlas.util

import au.org.ala.mobile.ozatlas.biocollect.dto.ProjectActivityAndUserRecords
import com.google.common.io.Resources
import com.squareup.moshi.Moshi
import okio.Okio
import org.junit.Ignore
import org.junit.Test

class BioCollectClientTest {

    // Simple test for diagnosing json deserialisation issues
    @Ignore @Test fun testDeserialiseJson() {
        val m = Moshi.Builder().add(MoshiTypeAdapters()).build()
        val a = m.adapter(ProjectActivityAndUserRecords::class.java)
        a.fromJson(Okio.buffer(Okio.source(Resources.getResource("input.json").openStream())))
    }
}