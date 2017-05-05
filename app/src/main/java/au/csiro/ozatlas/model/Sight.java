package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sad038 on 13/4/17.
 */

public class Sight {
    @Expose
    @SerializedName("embargoed")
    public String embargoed;
    @Expose
    @SerializedName("status")
    public String status;
    @Expose
    @SerializedName("lastUpdated")
    public String lastUpdated;
    @Expose
    @SerializedName("projectType")
    public String projectType;
    @Expose
    @SerializedName("projectId")
    public String projectId;
    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("activityId")
    public String activityId;
    @Expose
    @SerializedName("showCrud")
    public String showCrud;
    @Expose
    @SerializedName("activityOwnerName")
    public String activityOwnerName;
    @Expose
    @SerializedName("siteId")
    public String siteId;
    @Expose
    @SerializedName("thumbnailUrl")
    public String thumbnailUrl;
    @Expose
    @SerializedName("userId")
    public String userId;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("projectActivityId")
    public String projectActivityId;
    @Expose
    @SerializedName("records")
    public Records[] records;
    @Expose
    @SerializedName("projectName")
    public String projectName;

    public class Records {
        @Expose
        @SerializedName("eventDate")
        public String eventDate;
        @Expose
        @SerializedName("multimedia")
        public Multimedia[] multimedia;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("eventTime")
        public String eventTime;
        @Expose
        @SerializedName("occurrenceID")
        public String occurrenceID;
        @Expose
        @SerializedName("coordinates")
        public String[] coordinates;
    }

    public class Multimedia {
        @Expose
        @SerializedName("title")
        public String title;
        @Expose
        @SerializedName("imageId")
        public String imageId;
        @Expose
        @SerializedName("rights")
        public String rights;
        @Expose
        @SerializedName("rightsHolder")
        public String rightsHolder;
        @Expose
        @SerializedName("license")
        public String license;
        @Expose
        @SerializedName("type")
        public String type;
        @Expose
        @SerializedName("identifier")
        public String identifier;
        @Expose
        @SerializedName("documentId")
        public String documentId;
        @Expose
        @SerializedName("creator")
        public String creator;

    }
}
