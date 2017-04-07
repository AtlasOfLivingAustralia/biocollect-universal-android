package au.csiro.ozatlas.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by sad038 on 5/4/17.
 */

public interface ApiService {
    @GET("user/getKey")
    Observable<JsonObject> login(@Header("userName") String username, @Header("password") String password);
}
