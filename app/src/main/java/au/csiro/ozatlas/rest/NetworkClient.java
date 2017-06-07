package au.csiro.ozatlas.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import au.csiro.ozatlas.model.Tag;
import io.realm.RealmList;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sad038 on 18/4/17.
 */

/**
 * This class is to create
 * Network Client
 */
public class NetworkClient {
    private Retrofit retrofit;

    /**
     * constructor
     *
     * @param baseUrl
     */
    public NetworkClient(String baseUrl) {
        Type token = new TypeToken<RealmList<Tag>>() {
        }.getType();
        Gson gson = new GsonBuilder().registerTypeAdapter(token, new CustomTagTypeAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * constructor with custom gson
     *
     * @param baseUrl
     * @param gson
     */
    public NetworkClient(String baseUrl, Gson gson) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * create the OkHttpClient
     *
     * @return
     */
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new CustomRequestInterceptor())
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
