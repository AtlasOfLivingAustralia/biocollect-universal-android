package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.csiro.ozatlas.model.Project;

/**
 * Created by sad038 on 25/5/17.
 */

public class ProjectList {
    @Expose
    @SerializedName("total")
    public Integer total;

    @Expose
    @SerializedName("projects")
    public List<Project> projects;

    //@Expose
    //public Facets[] facets;
}
