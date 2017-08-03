package au.csiro.ozatlas.application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

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
public class BaseApplication extends MultiDexApplication {
    protected static AppComponent component;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

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

        //Google Analytics
        sAnalytics = GoogleAnalytics.getInstance(this);

        //initialize Dagger component
        component = init(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link BaseApplication}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}


