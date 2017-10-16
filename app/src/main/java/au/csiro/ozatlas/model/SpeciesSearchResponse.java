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
    public List<SearchSpecies> results;
}
