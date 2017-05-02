package au.csiro.ozatlas.rest;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.model.Tag;
import io.realm.RealmList;

/**
 * Created by sad038 on 2/5/17.
 */

public class CustomTagTypeAdapter extends TypeAdapter<RealmList<Tag>> {

    @Override
    public void write(JsonWriter out, RealmList<Tag> value) throws IOException {
        out.beginArray();
        for(Tag tag:value){
            out.value(tag.val);
        }
        out.endArray();
    }

    @Override
    public RealmList<Tag> read(JsonReader in) throws IOException {
        RealmList<Tag> list = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
            list.add(new Tag(in.nextString()));
        }
        in.endArray();
        return list;
    }
}
