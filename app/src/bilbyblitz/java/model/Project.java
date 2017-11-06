package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sad038 on 25/5/17.
 */

public class Project {
    @Expose
    public Boolean isContributingDataToAla;

    @Expose
    @SerializedName("startDate")
    public String startDate;

    @Expose
    @SerializedName("urlWeb")
    public String urlWeb;

    @Expose
    public Boolean isMERIT;

    @Expose
    public String endDate;

    @Expose
    public String difficulty;

    @Expose
    @SerializedName("projectType")
    public String projectType;

    @Expose
    @SerializedName("projectId")
    public String projectId;

    @Expose
    public String description;

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    public String plannedEndDate;

    @Expose
    public String[] tags;

    @Expose
    public String keywords;

    @Expose
    @SerializedName("organisationName")
    public String organisationName;

    @Expose
    public String[] scienceType;

    @Expose
    public String[] ecoScienceType;

    @Expose
    public String plannedStartDate;

    @Expose
    public String organisationId;

    @Expose
    public String noCost;

    @Expose
    @SerializedName("urlImage")
    public String urlImage;

    @Expose
    public String aim;

    @Expose
    @SerializedName("isExternal")
    public Boolean isExternal;

    @Expose
    public Boolean isSciStarter;
}
