package model;

import com.google.gson.annotations.Expose;

/**
 * Created by sad038 on 25/5/17.
 */

public class Facets {
    @Expose
    public String total;

    @Expose
    public String title;

    @Expose
    public Terms[] terms;

    @Expose
    public String name;

    @Expose
    public String state;

    @Expose
    public String helpText;
}
