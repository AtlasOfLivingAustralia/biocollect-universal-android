package au.csiro.ozatlas;

import android.app.Application;
import android.content.Context;

import au.csiro.ozatlas.di.AppComponent;
import au.csiro.ozatlas.di.AppModule;
import au.csiro.ozatlas.di.DaggerAppComponent;

/**
 * Created by sad038 on 5/4/17.
 */

public class OzAtlasApplication extends Application {
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = init(this);
    }

    public static AppComponent component() {
        return component;
    }

    public static AppComponent init(Context context) {
        return DaggerAppComponent.builder().appModule(new AppModule(context)).build();
    }
}


