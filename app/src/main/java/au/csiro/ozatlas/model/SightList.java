package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * response from GET sights
 */
public class SightList {
    @Expose
    @SerializedName("list")
    public List<Sight> list;
    @Expose
    @SerializedName("activities")
    public List<Sight> activities;
    @Expose
    @SerializedName("total")
    public Integer total;
}
