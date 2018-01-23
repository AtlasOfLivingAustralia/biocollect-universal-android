package rest;

import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.model.SightList;
import au.csiro.ozatlas.model.map.MapResponse;
import io.reactivex.Observable;
import model.map.MapModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
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
    //@GET("bioActivity/searchProjectActivities")
    @GET("ws/bioactivity/search")
    Observable<SightList> getSightings(@Query("fq") String fq, @Query("projectId") String id, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("view") String view, @Query("searchTerm") String searchTerm);

    //for multiple image upload
    @POST("ws/attachment/upload")
    Observable<ImageUploadResponse> uploadPhotos(@Body RequestBody files);

    //single image
    @Multipart
    @POST("ws/attachment/upload")
    Observable<ImageUploadResponse> uploadPhoto(@Part MultipartBody.Part file);

    //unique ID for uploading a sight
    //@POST("/ws/species/uniqueId")
    //Observable<JsonObject> getGUID();

    //uploading a sight
    @POST("ws/bioactivity/save")
    Observable<Response<Void>> postSightings(@Query("pActivityId") String pActivityId, @Body AddSight addSight);

    //uploading a map
    @POST("site/ajaxUpdate")
    Observable<MapResponse> postMap(@Body MapModel mapModel);
}
