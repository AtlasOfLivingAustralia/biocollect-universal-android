package au.org.ala.mobile.ozatlas.biocollect

import au.org.ala.mobile.ozatlas.biocollect.dto.ProjectActivityAndUserRecords
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface BioCollectClient {

    //http -j https://biocollect-test.ala.org.au/record/listProjectActivityAndUserRecords/d57961a1-517d-42f2-8446-c373c0c59579 userName:simon.bear@csiro.au authKey:b08edf1c-d7c8-45d6-ae8f-024b093cfa74
    @GET("record/listProjectActivityAndUserRecords/{projectActivityId}")
    @Headers("Accept: application/json")
    fun records(@Header("userName") userName : String, @Header("authKey") authKey : String, @Path("projectActivityId") projectActivityId: String) : Call<ProjectActivityAndUserRecords>

}