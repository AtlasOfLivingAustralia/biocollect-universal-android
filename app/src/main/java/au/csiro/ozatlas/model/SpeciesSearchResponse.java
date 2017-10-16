package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by sad038 on 18/4/17.
 */

/**
 * suggestion response for searching species
 */
public class SpeciesSearchResponse {
    @Expose
    @SerializedName("totalRecords")
    public Integer totalRecords;
    @Expose
    @SerializedName("results")
    public List<Species> results;

    public static class Species{
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("guid")
        public String guid;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("kingdom")
        public String kingdom;
        @Expose
        @SerializedName("highlight")
        public String highlight;
    }
}
