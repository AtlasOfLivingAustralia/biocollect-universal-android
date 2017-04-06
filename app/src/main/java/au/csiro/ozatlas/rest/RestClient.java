package au.csiro.ozatlas.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sad038 on 5/4/17.
 */

public class RestClient {
    private ApiService service;

    public RestClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new CustomRequestInterceptor())
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://demo2821545.mockable.io/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    public ApiService getService() {
        return service;
    }
}
