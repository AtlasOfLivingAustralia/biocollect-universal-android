package au.csiro.ozatlas.rest;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sad038 on 18/4/17.
 */

public class NetworkClient {
    private Retrofit retrofit;

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new CustomRequestInterceptor())
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public NetworkClient(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public NetworkClient(String baseUrl, Gson gson) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
