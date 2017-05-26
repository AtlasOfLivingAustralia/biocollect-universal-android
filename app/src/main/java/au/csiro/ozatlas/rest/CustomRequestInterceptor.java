package au.csiro.ozatlas.rest;

import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import application.CsiroApplication;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class adds the authKey and username with the header
 * of each Network Call
 */
public class CustomRequestInterceptor implements Interceptor {
    final String TAG = "RequestInterceptor";
    @Inject
    AtlasSharedPreferenceManager sharedPreferences;

    public CustomRequestInterceptor() {
        CsiroApplication.component().inject(this);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request;
        if (sharedPreferences.getAuthKey().equals("")) {
            request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
        } else {
            request = chain.request().newBuilder().addHeader("Accept", "application/json").addHeader("authKey", sharedPreferences.getAuthKey()).addHeader("userName", sharedPreferences.getUsername()).build();
        }

        long t1 = System.nanoTime();
        Log.d(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.d(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}

