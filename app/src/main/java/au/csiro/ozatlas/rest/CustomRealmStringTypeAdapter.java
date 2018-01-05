package au.csiro.ozatlas.rest;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import au.csiro.ozatlas.model.RealmString;
import io.realm.RealmList;

/**
 * Created by sad038 on 2/5/17.
 */

public class CustomRealmStringTypeAdapter extends TypeAdapter<RealmList<RealmString>> {

    @Override
    public void write(JsonWriter out, RealmList<RealmString> value) throws IOException {
        out.beginArray();
        if (value != null)
            for (RealmString realmString : value) {
                out.value(realmString.val);
            }
        out.endArray();

    }

    @Override
    public RealmList<RealmString> read(JsonReader in) throws IOException {
        RealmList<RealmString> list = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
            list.add(new RealmString(in.nextString()));
        }
        in.endArray();
        return list;
    }
}
