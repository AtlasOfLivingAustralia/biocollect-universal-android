package application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.application.BaseApplication;
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
public class CsiroApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this);
    }
}


