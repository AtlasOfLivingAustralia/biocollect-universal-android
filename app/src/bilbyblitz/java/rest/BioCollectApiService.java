package rest;


import io.reactivex.Observable;
import model.ProjectList;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sad038 on 5/4/17.
 */

public interface BioCollectApiService {
    @GET("/ws/project/search?sort=nameSort&hub=clc-tracker&fq=isExternal:F")
    Observable<ProjectList> getProjects(@Query("initiator") String initiator, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("sort") String sort, @Query("q") String q, @Query("isUserPage") Boolean isUserPage);

    /*@GET("bioActivity/searchProjectActivities")
    Observable<SightList> getSightings(@Query("projectId") String id, @Query("max") Integer max, @Query("offset") Integer offset, @Query("mobile") Boolean mobile, @Query("view") String view, @Query("searchTerm") String searchTerm, @Query("userId") String userId);

    @GET("ws/survey/list/{projectId}")
    Observable<List<Survey>> getSurveys(@Path("projectId") String id);*/
}
