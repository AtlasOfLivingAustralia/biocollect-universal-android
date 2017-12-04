package au.csiro.ozatlas.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.model.SpeciesSearchResponse;

/**
 * Created by sad038 on 18/4/17.
 */

/**
 * Deserializer for the sepecies search result
 */
public class SearchSpeciesSerializer implements JsonDeserializer<List<SearchSpecies>> {

    @Override
    public List<SearchSpecies> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonElement;
        Log.d("SearchSpeciesSerializer", "in SearchSpeciesSerializer");
        //checking the response whether it has "searchResults" as the begining object name.
        //if it does it removes the name and serialize its value
        if (jsonObject.has("searchResults")) {
            jsonObject = jsonObject.getAsJsonObject("searchResults");
            Log.d("SearchSpeciesSerializer", "searchResults removed" + jsonObject);
        }

        if (jsonObject.has("results")) {
            jsonElement = jsonObject.getAsJsonObject().get("results");
            Log.d("SearchSpeciesSerializer", "results removed" + jsonElement);
        } else {
            jsonElement = json;
        }

        return new Gson().fromJson(jsonElement, new TypeToken<List<SearchSpecies>>() {}.getType());
    }
}
