package au.csiro.ozatlas.model;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

public class SightingPhoto extends RealmObject{
    public SightingPhoto() {
    }

    @Expose
    @SerializedName("dateTaken")
    public String dateTaken;
    @Expose
    @SerializedName("licence")
    public String licence;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("thumbnailUrl")
    public String thumbnailUrl;
    @Expose
    @SerializedName("filename")
    public String filename;
    @Expose
    @SerializedName("contentType")
    public String contentType;
    @Expose
    @SerializedName("staged")
    public Boolean staged;
    @Expose
    @SerializedName("notes")
    public String notes;
    @Expose
    @SerializedName("url")
    public String url;
    @Expose
    @SerializedName("attribution")
    public String attribution;
    @Expose
    @SerializedName("licensePosition")
    public int licensePosition;

    //Not part of Json
    public String filePath;
}

