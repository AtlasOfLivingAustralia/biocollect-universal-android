package au.csiro.ozatlas.rest;

import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by sad038 on 5/4/17.
 */

public interface ApiService {
    @GET("58e5f82c1100003210bfc6a0")
    Observable<String> register();
}
