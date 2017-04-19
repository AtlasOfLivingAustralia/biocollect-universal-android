package au.csiro.ozatlas.rest;

import retrofit2.Retrofit;

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
