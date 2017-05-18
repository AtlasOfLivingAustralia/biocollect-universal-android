package au.csiro.ozatlas.di;

import android.content.Context;

import javax.inject.Singleton;

import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sad038 on 5/4/17.
 */

/**
 * Injecting SharedPreference
 */
@Module
public class PreferenceModule {

    private Context context;

    public PreferenceModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    AtlasSharedPreferenceManager providePreference() {
        return new AtlasSharedPreferenceManager(context);
    }
}
