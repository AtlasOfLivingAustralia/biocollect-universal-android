package au.csiro.ozatlas.di;

import javax.inject.Singleton;

import au.csiro.ozatlas.rest.RestClient;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sad038 on 5/4/17.
 */

@Module
public class RestModule {
    @Singleton
    @Provides
    RestClient getRestClient() {
        return new RestClient();
    }
}
