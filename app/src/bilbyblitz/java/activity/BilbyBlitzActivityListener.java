package activity;

import au.csiro.ozatlas.manager.Language;

/**
 * Created by sad038 on 10/11/17.
 */

public interface BilbyBlitzActivityListener {
    String localisedString(String key, int defaultRes);

    String localisedString(String key);

    void loadLanguageFile(String fileName, Language language);
}
