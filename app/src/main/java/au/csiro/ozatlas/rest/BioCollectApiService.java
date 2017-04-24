package au.csiro.ozatlas.rest;

import au.csiro.ozatlas.model.get.SightList;
import au.csiro.ozatlas.model.post.AddSight;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by sad038 on 5/4/17.
 */

public interface BioCollectApiService {
    @GET("bioActivity/searchProjectActivities")
    Observable<SightList> getSightings(@Query("projectId") String id, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("view") String view, @Query("searchTerm") String searchTerm);

    @POST("ws/attachment/upload")
    Observable<Void> uploadPhotos(@Body AddSight addSight);

    @POST("ws/bioactivity/save")
    Observable<Void> postSightings(@Query("pActivityId") String pActivityId, @Body AddSight addSight);
}
