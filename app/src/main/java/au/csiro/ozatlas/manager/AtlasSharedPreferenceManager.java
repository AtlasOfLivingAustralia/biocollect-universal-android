package au.csiro.ozatlas.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sad038 on 5/4/17.
 */

public class AtlasSharedPreferenceManager {
    private SharedPreferences sharedPreferences;

    /**
     * constructor
     * @param context
     */
    public AtlasSharedPreferenceManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * write auth key
     * @param authKey
     */
    public void writeAuthKey(String authKey) {
        sharedPreferences.edit().putString("AUTH_KEY", authKey).apply();
    }

    /**
     * get the auth key
     * @return
     */
    public String getAuthKey() {
        return sharedPreferences.getString("AUTH_KEY", "");
    }

    /**
     * write user's display name
     * @param name
     */
    public void writeUserDisplayName(String name) {
        sharedPreferences.edit().putString("USER_DISPLAY_NAME", name).apply();
    }

    /**
     * get users display name
     * @return
     */
    public String getUserDisplayName() {
        return sharedPreferences.getString("USER_DISPLAY_NAME", "");
    }

    /**
     * write user's username
     * @param email
     */
    public void writeUsername(String email) {
        sharedPreferences.edit().putString("USER_NAME", email).apply();
    }

    /**
     * get the user's username
     * @return
     */
    public String getUsername() {
        return sharedPreferences.getString("USER_NAME", "");
    }

}
