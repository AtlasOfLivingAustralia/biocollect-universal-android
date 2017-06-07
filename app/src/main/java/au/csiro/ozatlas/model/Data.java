package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

/**
 * Part of Upload Sight
 * AddSight - > Outputs
 */
public class Data extends RealmObject {
    @Expose
    @SerializedName("tags")
    public RealmList<Tag> tags;
    @Expose
    @SerializedName("locationLatitude")
    public Double locationLatitude;
    @Expose
    @SerializedName("species")
    public Species species;
    @Expose
    @SerializedName("surveyStartTime")
    public String surveyStartTime;
    @Expose
    @SerializedName("recordedBy")
    public String recordedBy;
    @Expose
    @SerializedName("individualCount")
    public Integer individualCount;
    @Expose
    @SerializedName("locationLongitude")
    public Double locationLongitude;
    @Expose
    @SerializedName("identificationConfidence")
    public String identificationConfidence;
    @Expose
    @SerializedName("sightingPhoto")
    public RealmList<SightingPhoto> sightingPhoto;
    @Expose
    @SerializedName("surveyDate")
    public String surveyDate;

    public Data() {
    }
}
