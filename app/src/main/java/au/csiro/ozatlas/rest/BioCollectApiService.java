package au.csiro.ozatlas.rest;

import com.google.gson.JsonObject;

import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.model.get.SightList;
import au.csiro.ozatlas.model.post.AddSight;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by sad038 on 5/4/17.
 */

public interface BioCollectApiService {
    @GET("bioActivity/searchProjectActivities")
    Observable<SightList> getSightings(@Query("projectId") String id, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("view") String view, @Query("searchTerm") String searchTerm);

    //for multiple image upload
    @POST("ws/attachment/upload")
    Observable<ImageUploadResponse> uploadPhotos(@Body RequestBody files);

    //single image
    @Multipart
    @POST("ws/attachment/upload")
    Observable<ImageUploadResponse> uploadPhoto(@Part MultipartBody.Part file);

    @POST("/ws/species/uniqueId")
    Observable<JsonObject> getGUID();

    @POST("ws/bioactivity/save")
    Observable<Void> postSightings(@Query("pActivityId") String pActivityId, @Body AddSight addSight);
}
