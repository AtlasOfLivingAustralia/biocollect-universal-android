package au.csiro.ozatlas.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import au.csiro.ozatlas.model.Project;
import au.csiro.ozatlas.model.SpeciesFilter;

/**
 * Created by sad038 on 5/4/17.
 */

public class AtlasSharedPreferenceManager {
    private SharedPreferences sharedPreferences;

    /**
     * constructor
     *
     * @param context
     */
    public AtlasSharedPreferenceManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * constructor
     *
     * @param sharedPreferences
     */
    public AtlasSharedPreferenceManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * get the shared preference
     *
     * @return
     */
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * write auth key
     *
     * @param authKey
     */
    public void writeAuthKey(String authKey) {
        sharedPreferences.edit().putString("AUTH_KEY", authKey).apply();
    }

    /**
     * get the auth key
     *
     * @return
     */
    public String getAuthKey() {
        return sharedPreferences.getString("AUTH_KEY", "");
    }

    /**
     * Write AuthState
     */
    public void writeAuthState(AuthState state) {
        sharedPreferences.edit().putString("AUTH_STATE", state.jsonSerializeString()).apply();
    }

    /**
     * Get AuthState
     */
    public AuthState getAuthState() {
        try {
            String authStateString = sharedPreferences.getString("AUTH_STATE", "");
            if (authStateString == null || authStateString.length() == 0) {
                return null;
            } else {
                AuthState.jsonDeserialize(authStateString);
            }
        } catch (JSONException ex) {
            Log.e("Preference Manager", Log.getStackTraceString(ex));
        }

        return null;
    }

    /**
     * write the OIDC discovery document
     * @param discovery
     */
    public void writeAuthServiceConfig(AuthorizationServiceConfiguration discovery) {
        sharedPreferences.edit().putString("AUTH_OIDC_DISCOVERY", discovery.toJsonString()).apply();
    }

    /**
     * get the OIDC discovery document
     */
    public AuthorizationServiceConfiguration getAuthServiceConfig() {
        try {
            return AuthorizationServiceConfiguration.fromJson(sharedPreferences.getString("AUTH_OIDC_DISCOVERY", ""));
        } catch (JSONException err) {
            Log.w("Shared Preferences", "No AuthorizationServiceConfiguration has been stored, returning null...");
        }

        return null;
    }

    /**
     * write userId
     *
     * @param userId
     */
    public void writeUserId(String userId) {
        sharedPreferences.edit().putString("USER_ID", userId).apply();
    }

    /**
     * get the userId
     *
     * @return
     */
    public String getUserId() {
        return sharedPreferences.getString("USER_ID", "");
    }

    /**
     * write user's display name
     *
     * @param name
     */
    public void writeUserDisplayName(String name) {
        sharedPreferences.edit().putString("USER_DISPLAY_NAME", name).apply();
    }

    /**
     * get the language
     *
     * @return
     */
    public String getLanguage() {
        return sharedPreferences.getString("LANGUAGE", "");
    }

    /**
     * write user's selected language
     *
     * @param language
     */
    public void writeSelectedLanguage(String language) {
        sharedPreferences.edit().putString("LANGUAGE", language).apply();
    }

    /**
     * write user's selected language
     *
     * @param language
     */
    public void writeSelectedEnumLanguage(Language language) {
        sharedPreferences.edit().putString("LANGUAGE_ENUM", language.toString()).apply();
    }

    /**
     * get the language filename
     *
     * @return
     */
    public String getLanguageFileName() {
        return sharedPreferences.getString("LANGUAGE_FILENAME", "");
    }

    /**
     * get the language filename
     *
     * @return
     */
    public Language getLanguageEnumLanguage() {
        return Language.valueOf(sharedPreferences.getString("LANGUAGE_ENUM", Language.ENGLISH.toString()));
    }

    /**
     * write user's selected language's filename
     *
     * @param languageFileName
     */
    public void writeSelectedLanguageFileName(String languageFileName) {
        sharedPreferences.edit().putString("LANGUAGE_FILENAME", languageFileName).apply();
    }

    /**
     * get the project
     *
     * @return
     */
    public Project getSelectedProject() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("PROJECT", "");
        return gson.fromJson(json, Project.class);
    }

    /**
     * write user's selected project
     *
     * @param project
     */
    public void writeSelectedProject(Project project) {
        Gson gson = new Gson();
        String json = gson.toJson(project);
        sharedPreferences.edit().putString("PROJECT", json).apply();
    }

    /**
     * get users display name
     *
     * @return
     */
    public String getUserDisplayName() {
        return sharedPreferences.getString("USER_DISPLAY_NAME", "");
    }

    /**
     * write user's username
     *
     * @param email
     */
    public void writeUsername(String email) {
        sharedPreferences.edit().putString("USER_NAME", email).apply();
    }

    /**
     * get the user's username
     *
     * @return
     */
    public String getUsername() {
        return sharedPreferences.getString("USER_NAME", "");
    }


    /**
     * get the species filter for bilby blitz
     *
     * @return
     */
    public SpeciesFilter getSpeciesFilter() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SPECIES_FILTER", "");
        return gson.fromJson(json, SpeciesFilter.class);
    }

    /**
     * write the species filter  for bilby blitz
     *
     * @param speciesFilter
     */
    public void writeSpeciesFilter(SpeciesFilter speciesFilter) {
        Gson gson = new Gson();
        String json = gson.toJson(speciesFilter);
        sharedPreferences.edit().putString("SPECIES_FILTER", json).apply();
    }

    public Map getHeaderMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        String key = getAuthState().getAccessToken();
        if (!key.equals(""))
            map.put("Authorization", String.format("Bearer %s", key));
        String username = getUsername();
        if (!username.equals(""))
            map.put("userName", username);
        return map;
    }
}
