package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 16/10/17.
 */
@Parcel
@RealmClass
public class SearchSpecies extends RealmObject {
    @PrimaryKey
    public String realmId;
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    @SerializedName("guid")
    public String guid;
    @Expose
    @SerializedName("outputSpeciesId")
    public String outputSpeciesId;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("commonName")
    public String commonName;
    @Expose
    @SerializedName("scientificName")
    public String scientificName;
    @Expose
    @SerializedName("kingdom")
    public String kingdom;
    @Expose
    @SerializedName("highlight")
    public String highlight;
}
