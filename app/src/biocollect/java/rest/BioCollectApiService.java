package rest;

import com.google.gson.JsonObject;

import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.model.SightList;
import io.reactivex.Observable;
import model.ProjectList;
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
    @GET("/ws/project/search?initiator=biocollect&sort=nameSort")
    Observable<ProjectList> getProjects(@Query("initiator") String initiator, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("sort") String sort, @Query("q") String q, @Query("isUserPage") Boolean isUserPage);
}
