package au.csiro.ozatlas.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sad038 on 5/4/17.
 */

public class AtlasSharedPreferenceManager {
    private SharedPreferences sharedPreferences;

    public AtlasSharedPreferenceManager(Context context){
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
