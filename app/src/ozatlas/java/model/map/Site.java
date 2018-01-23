package model.map;

import com.google.gson.annotations.Expose;

/**
 * Created by sad038 on 7/12/17.
 */

public class Site {
    @Expose
    public String[] projects;

    @Expose
    public Extent extent;

    @Expose
    public String name;

    @Expose
    public String siteId;

    @Expose
    public String visibility;

    @Expose
    public String asyncUpdate;
}
