package language;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import au.csiro.ozatlas.manager.AtlasCallback;
import au.csiro.ozatlas.manager.Utils;

/**
 * Created by sad038 on 8/11/17.
 */

public class LanguageManager {
    public static JSONObject languageJSON;

    /**
     * get the value of given key from language
     *
     * @param context
     * @param key
     * @param defaultRes from resource XML
     * @return
     */
    public static String localizedString(Context context, String key, int defaultRes) {
        if (languageJSON != null) {
            String value = key;
            try {
                value = languageJSON.getString(key);
            } catch (JSONException e) {
                value = key;
            } catch (Exception n) {
                Log.e("LanguageManager", n.getMessage());
            }
            return value;
        }
        return context.getString(defaultRes);
    }
}
