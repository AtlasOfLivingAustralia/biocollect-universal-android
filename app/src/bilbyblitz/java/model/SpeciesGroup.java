package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sad038 on 24/10/17.
 */

public class SpeciesGroup implements Serializable {
    @Expose
    @SerializedName("isAuthoritative")
    public Boolean isAuthoritative;
    @Expose
    @SerializedName("listName")
    public String listName;
    @Expose
    @SerializedName("listType")
    public String listType;
    @Expose
    @SerializedName("lastUpdated")
    public String lastUpdated;
    @Expose
    @SerializedName("isThreatened")
    public Boolean isThreatened;
    @Expose
    @SerializedName("username")
    public String username;
    @Expose
    @SerializedName("isInvasive")
    public Boolean isInvasive;
    @Expose
    @SerializedName("dateCreated")
    public String dateCreated;
    @Expose
    @SerializedName("itemCount")
    public Integer itemCount;
    @Expose
    @SerializedName("dataResourceUid")
    public String dataResourceUid;

}
