package application;

import android.app.Application;
import android.content.Context;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.di.PreferenceModule;
import au.csiro.ozatlas.di.RestModule;
import di.AppComponent;
import di.DaggerAppComponent;
import io.realm.Realm;

/**
 * Created by sad038 on 5/4/17.
 */

/**
 * Application class to initialise Realm
 * and Dagger
 */
public class CsiroApplication extends Application {
    private static AppComponent component;

    public static AppComponent component() {
        return component;
    }

    public static AppComponent init(Context context) {
        return DaggerAppComponent.builder()
                .restModule(new RestModule(context.getString(R.string.biocollect_url)))
                .preferenceModule(new PreferenceModule(context))
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Realm. Should only be done once when the application starts.
        //Realm.init(this);

        //initialize Dagger component
        component = init(this);
    }
}


