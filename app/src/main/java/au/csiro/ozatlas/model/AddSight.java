package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sad038 on 24/4/17.
 */

/**
 * Model to upload a Sight
 */
public class AddSight extends RealmObject {
    @PrimaryKey
    public Long realmId;
    public boolean upLoading;

    @Expose
    @SerializedName("mainTheme")
    public String mainTheme;
    @Expose
    @SerializedName("activityId")
    public String activityId;
    @Expose
    @SerializedName("siteId")
    public String siteId;
    @Expose
    @SerializedName("projectId")
    public String projectId;
    @Expose
    @SerializedName("outputs")
    public RealmList<Outputs> outputs;
    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("projectStage")
    public String projectStage;
}




