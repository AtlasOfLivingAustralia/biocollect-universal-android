package rest;


import au.csiro.ozatlas.model.ImageUploadResponse;
import io.reactivex.Observable;
import model.ProjectList;
import model.map.MapModel;
import model.track.TrackModel;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sad038 on 5/4/17.
 */

public interface BioCollectApiService {
    @GET("/ws/project/search?sort=nameSort&hub=clc-tracker&fq=isExternal:F")
    Observable<ProjectList> getProjects(@Query("initiator") String initiator, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("sort") String sort, @Query("q") String q, @Query("isUserPage") Boolean isUserPage);

    //single image
    @Multipart
    @POST("ws/attachment/upload")
    Observable<ImageUploadResponse> uploadPhoto(@Part MultipartBody.Part file);

    //uploading a sight
    @POST("ws/bioactivity/save")
    Observable<Response<Void>> postTracks(@Query("pActivityId") String pActivityId, @Body TrackModel trackModel);

    //uploading a map
    @POST("site/ajaxUpdate/{id}")
    Observable<Response<Void>> postMap(@Body MapModel mapModel, @Path("id") String id);

    /*@GET("bioActivity/searchProjectActivities")
    Observable<SightList> getSightings(@Query("projectId") String id, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("view") String view, @Query("searchTerm") String searchTerm, @Query("userId") String userId);

    @GET("ws/survey/list/{projectId}")
    Observable<List<Survey>> getSurveys(@Path("projectId") String id);*/
}
