package au.csiro.ozatlas.rest;

import retrofit2.Retrofit;

/**
 * Created by sad038 on 5/4/17.
 */

public class RestClient {
    private BioCollectApiService service;

    public RestClient(String baseUrl) {
        Retrofit retrofit = new NetworkClient(baseUrl).getRetrofit();
        service = retrofit.create(BioCollectApiService.class);
    }

    public BioCollectApiService getService() {
        return service;
    }
}
