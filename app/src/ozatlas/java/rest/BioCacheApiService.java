package rest;

import java.util.List;

import io.reactivex.Observable;
import model.ExploreAnimal;
import model.ExploreGroup;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sad038 on 28/6/17.
 */

public interface BioCacheApiService {
    //map explore
    //http://biocache.ala.org.au/ws/explore/groups.json?fq=geospatial_kosher%3Atrue&facets=species_group&lat=27.76&lon=138.55&radius=532
    @GET("ws/explore/groups.json")
    Observable<List<ExploreGroup>> getSpeciesGroupFromMap(@Query("fq") String fq, @Query("facets") String facets, @Query("lat") Double lat, @Query("lon") Double lon, @Query("radius") Double radius);

    //http://biocache.ala.org.au/ws/explore/group/Animals?fq=geospatial_kosher%3Atrue&facets=species_group&lat=27.76&lon=138.55&radius=532
    @GET("ws/explore/group/{group}")
    Observable<List<ExploreAnimal>> getSpeciesAnimalFromMap(@Path("group") String group, @Query("fq") String fq, @Query("facets") String facets, @Query("lat") Double lat, @Query("lon") Double lon, @Query("radius") Double radius, @Query("pageSize") Integer pageSize, @Query("start") Integer start);
}
