package au.csiro.ozatlas.rest;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import au.csiro.ozatlas.model.SpeciesSearchResponse;

/**
 * Created by sad038 on 18/4/17.
 */

public class SearchSpeciesSerializer implements JsonDeserializer<SpeciesSearchResponse> {

    @Override
    public SpeciesSearchResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonElement;
        if (jsonObject.has("searchResults")) {
            jsonElement = json.getAsJsonObject().get("searchResults");
        } else {
            jsonElement = json;
        }
        return new Gson().fromJson(jsonElement, SpeciesSearchResponse.class);
    }
}
