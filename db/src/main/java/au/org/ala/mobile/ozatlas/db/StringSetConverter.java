package au.org.ala.mobile.ozatlas.db;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;


public class StringSetConverter implements PropertyConverter<Set<String>, String> {

    private static final String LOG_TAG = "StringSetConverter";
    private static final Moshi moshi = new Moshi.Builder().build();
    private static final JsonAdapter<Set<String>> adapter = moshi.adapter(Types.newParameterizedType(Set.class, String.class));

    @Override
    public Set<String> convertToEntityProperty(String strings) {
        if (strings == null) return null;
        try {
            return adapter.fromJson(strings);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Couldn't deserialise " + strings);
            return new LinkedHashSet<String>();
        }
    }

    @Override
    public String convertToDatabaseValue(Set<String> strings) {
        if (strings == null) return null;
        return adapter.toJson(strings);
    }
}