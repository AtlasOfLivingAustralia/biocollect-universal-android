package application;

import au.csiro.ozatlas.application.BaseApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}


