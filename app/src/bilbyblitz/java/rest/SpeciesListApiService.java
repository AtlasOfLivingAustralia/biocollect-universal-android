package rest;

import java.util.List;

import au.csiro.ozatlas.model.SearchSpecies;
import io.reactivex.Observable;
import model.SpeciesGroupList;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sad038 on 24/10/17.
 */

public interface SpeciesListApiService {
    //https://lists.ala.org.au/ws/speciesList?max=10&offset=0
    @GET("/ws/speciesList")
    Observable<SpeciesGroupList> getGroupList(@Query("max") Integer max, @Query("offset") Integer offset);

    //http://lists.ala.org.au/ws/speciesListItems/dr1266
    @GET("/ws/speciesListItems/{dataResourceId}?includeKVP=true")
    Observable<List<SearchSpecies>> getSpeciesList(@Path("dataResourceId") String dataResourceId);
}
