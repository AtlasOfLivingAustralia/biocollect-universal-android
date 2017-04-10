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

    public void writeAuthKey(String authKey){
        sharedPreferences.edit().putString("AUTH_KEY", authKey).apply();
    }

    public String getAuthKey(){
        return sharedPreferences.getString("AUTH_KEY", null);
    }

    public void writeUsername(String email){
        sharedPreferences.edit().putString("USER_NAME", email).apply();
    }

    public String getUsername(){
        return sharedPreferences.getString("USER_NAME", "");
    }

}
