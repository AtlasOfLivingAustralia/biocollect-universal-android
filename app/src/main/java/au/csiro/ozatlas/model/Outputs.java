package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

/**
 * Part of Upload Sight [AddSight]
 */
public class Outputs extends RealmObject {
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("data")
    public Data data;
    @Expose
    @SerializedName("outputId")
    public String outputId;
    @Expose
    @SerializedName("outputNotCompleted")
    public String outputNotCompleted;

    public Outputs() {
    }
}
