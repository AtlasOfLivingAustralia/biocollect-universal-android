package au.csiro.ozatlas.rest;

import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomRequestInterceptor implements Interceptor {
    final String TAG = "RequestInterceptor";
    @Inject
    AtlasSharedPreferenceManager sharedPreferences;

    public CustomRequestInterceptor() {
        OzAtlasApplication.component().inject(this);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().header("Accept", "application/json").header("authKey", sharedPreferences.getAuthKey()).header("userName", sharedPreferences.getUsername()).build();

        long t1 = System.nanoTime();
        Log.d(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.d(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}

