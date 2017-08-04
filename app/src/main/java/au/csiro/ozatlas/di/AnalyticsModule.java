package au.csiro.ozatlas.di;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import au.csiro.ozatlas.application.BaseApplication;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sad038 on 5/4/17.
 */

/**
 * Injecting Google Analytics Tracker
 */
@Module
public class AnalyticsModule {

    private Context context;

    public AnalyticsModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    FirebaseAnalytics provideAnalyticsTracker() {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.getFirebaseAnalytics();
    }
}
