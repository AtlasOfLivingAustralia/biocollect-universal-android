package au.csiro.ozatlas.rest;

import au.csiro.ozatlas.model.SpeciesSearchResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sad038 on 18/4/17.
 */

public interface BieApiService {
    @GET("ws/search.json")
    Observable<SpeciesSearchResponse> searchSpecies(@Query("q") String q);
}
