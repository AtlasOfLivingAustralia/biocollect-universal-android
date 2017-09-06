package au.csiro.ozatlas.rest;

import rest.BioControlHubApiService;
import retrofit2.Retrofit;

/**
 * Created by sad038 on 5/4/17.
 */

public class RestClient {
    private BioControlHubApiService service;

    public RestClient(String baseUrl) {
        Retrofit retrofit = new NetworkClient(baseUrl).getRetrofit();
        service = retrofit.create(BioControlHubApiService.class);
    }

    public BioControlHubApiService getService() {
        return service;
    }
}
