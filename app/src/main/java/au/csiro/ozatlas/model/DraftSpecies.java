package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

/**
 * Part of uploading AddSight
 */
public class DraftSpecies extends RealmObject {
    @Expose
    @SerializedName("guid")
    public String guid;
    @Expose
    @SerializedName("commonName")
    public String commonName;
    @Expose
    @SerializedName("outputSpeciesId")
    public String outputSpeciesId;
    @Expose
    @SerializedName("scientificName")
    public String scientificName;
    @Expose
    @SerializedName("name")
    public String name;

    public DraftSpecies() {
    }
}
