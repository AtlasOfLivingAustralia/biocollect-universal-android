package model;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by sad038 on 25/5/17.
 */

public class ProjectList {
    @Expose
    public Integer total;

    @Expose
    public List<Projects> projects;

    //@Expose
    //public Facets[] facets;
}
