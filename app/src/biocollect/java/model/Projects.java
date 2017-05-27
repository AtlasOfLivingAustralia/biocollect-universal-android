package model;

import com.google.gson.annotations.Expose;

/**
 * Created by sad038 on 25/5/17.
 */

public class Projects {
    @Expose
    public Boolean isContributingDataToAla;

    @Expose
    public String startDate;

    @Expose
    public String urlWeb;

    @Expose
    public Boolean isMERIT;

    @Expose
    public String endDate;

    @Expose
    public String difficulty;

    @Expose
    public String projectType;

    @Expose
    public String projectId;

    @Expose
    public String description;

    @Expose
    public String name;

    @Expose
    public String plannedEndDate;

    @Expose
    public String[] tags;

    @Expose
    public String keywords;

    @Expose
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
    public Link[] links;

    @Expose
    public String noCost;

    @Expose
    public String urlImage;

    @Expose
    public Coverage coverage;

    @Expose
    public String aim;

    @Expose
    public Boolean isExternal;

    @Expose
    public Boolean isSciStarter;
}
