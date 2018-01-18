package au.csiro.ozatlas.rest;

import java.util.List;

import au.csiro.ozatlas.model.SearchSpecies;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sad038 on 18/4/17.
 */

public interface BieApiService {
    //https://bie.ala.org.au/ws/search?q=sathon&fq=taxonomicStatus:accepted
    @GET("ws/search.json")
    Observable<List<SearchSpecies>> searchSpecies(@Query("q") String q, @Query("fq") String fq);
}
