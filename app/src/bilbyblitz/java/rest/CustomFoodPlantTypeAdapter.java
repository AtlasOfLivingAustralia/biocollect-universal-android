package rest;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.realm.RealmList;
import model.track.FoodPlant;

/**
 * Created by sad038 on 2/5/17.
 */

public class CustomFoodPlantTypeAdapter extends TypeAdapter<RealmList<FoodPlant>> {

    @Override
    public void write(JsonWriter out, RealmList<FoodPlant> value) throws IOException {
        out.beginArray();
        for (FoodPlant foodPlant : value) {
            out.value(foodPlant.val);
        }
        out.endArray();
    }

    @Override
    public RealmList<FoodPlant> read(JsonReader in) throws IOException {
        RealmList<FoodPlant> list = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
            list.add(new FoodPlant(in.nextString()));
        }
        in.endArray();
        return list;
    }
}
