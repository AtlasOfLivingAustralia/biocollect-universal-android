package rest;

import com.google.gson.JsonObject;

import java.util.List;

import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.model.SightList;
import io.reactivex.Observable;
import model.ExploreAnimal;
import model.ExploreGroup;
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
    @GET("bioActivity/searchProjectActivities")
    Observable<SightList> getSightings(@Query("projectId") String id, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("view") String view, @Query("searchTerm") String searchTerm);

    //for multiple image upload
    @POST("ws/attachment/upload")
    Observable<ImageUploadResponse> uploadPhotos(@Body RequestBody files);

    //single image
    @Multipart
    @POST("ws/attachment/upload")
    Observable<ImageUploadResponse> uploadPhoto(@Part MultipartBody.Part file);

    //unique ID for uploading a sight
    @POST("/ws/species/uniqueId")
    Observable<JsonObject> getGUID();

    //uploading a sight
    @POST("ws/bioactivity/save")
    Observable<Response<Void>> postSightings(@Query("pActivityId") String pActivityId, @Body AddSight addSight);

    //map explore
    //http://biocache.ala.org.au/ws/explore/groups.json?fq=geospatial_kosher%3Atrue&facets=species_group&lat=27.76&lon=138.55&radius=532
    @GET("ws/explore/groups.json")
    Observable<List<ExploreGroup>> getSpeciesGroupFromMap(@Query("fq") String fq, @Query("facets") String facets, @Query("lat") Double lat, @Query("lon") Double lon, @Query("radius") Integer radius);

    //http://biocache.ala.org.au/ws/explore/group/Animals?fq=geospatial_kosher%3Atrue&facets=species_group&lat=27.76&lon=138.55&radius=532
    @GET("ws/explore/group/Animals")
    Observable<List<ExploreAnimal>> getSpeciesAnimalFromMap(@Query("fq") String fq, @Query("facets") String facets, @Query("lat") Double lat, @Query("lon") Double lon, @Query("radius") Integer radius);
}
