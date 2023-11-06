package au.csiro.ozatlas.application;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.di.AnalyticsModule;
import au.csiro.ozatlas.di.PreferenceModule;
import au.csiro.ozatlas.di.RestModule;
import di.AppComponent;
import di.DaggerAppComponent;

/**
 * Created by sad038 on 5/4/17.
 */

/**
 * Application class to initialise Realm
 * and Dagger
 */
@SuppressLint("Registered")
public class BaseApplication extends MultiDexApplication {
    protected static AppComponent component;
    /**
     * The {@code FirebaseAnalytics} used to record screen views.
     */
    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    // [END declare_analytics]

    public static AppComponent component() {
        return component;
    }

    public static AppComponent init(Context context) {
        return DaggerAppComponent.builder()
                .restModule(new RestModule(context.getString(R.string.biocollect_url)))
                .preferenceModule(new PreferenceModule(context))
                .analyticsModule(new AnalyticsModule(context))
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //initialize Dagger component
        component = init(this);
    }

    /**
     * @return Firebase analytics instance
     */
    public FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
}


