package au.csiro.ozatlas.rest;

import au.csiro.ozatlas.model.LoginResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by sad038 on 5/4/17.
 */

public interface EcoDataApiService {
    @GET("user/getKey")
    Observable<LoginResponse> login(@Header("userName") String username, @Header("password") String password);
}
