package au.csiro.ozatlas.rest;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sad038 on 5/4/17.
 */

public class RestClient {
    private ApiService service;

    public RestClient(String baseUrl) {
        Retrofit retrofit = new NetworkClient(baseUrl).getRetrofit();
        service = retrofit.create(ApiService.class);
    }

    public ApiService getService() {
        return service;
    }
}
