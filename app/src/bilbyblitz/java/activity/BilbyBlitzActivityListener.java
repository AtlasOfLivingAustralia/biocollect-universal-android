package activity;

import fragments.setting.Language;

/**
 * Created by sad038 on 10/11/17.
 */

public interface BilbyBlitzActivityListener {
    String localisedString(String key, int defaultRes);

    void loadLanguageFile(String fileName, Language language);
}
