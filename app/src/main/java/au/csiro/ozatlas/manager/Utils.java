package au.csiro.ozatlas.manager;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sad038 on 28/9/17.
 */

public class Utils {
    public static String nullCheck(String str) {
        if (str == null)
            return "";
        return str;
    }

    /**
     * search a string in a string array
     * @param strings array to search in
     * @param searchString string to be searched
     * @return
     */
    public static int stringSearchInArray(String[] strings, String searchString) {
        int position = -1;

        for (int i = 0; i < strings.length; i++) {
            if (searchString.equals(strings[i])) {
                position = i;
                break;
            }
        }
        return position;
    }
}
