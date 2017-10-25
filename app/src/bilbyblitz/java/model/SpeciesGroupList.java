package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sad038 on 24/10/17.
 */

public class SpeciesGroupList {
    @Expose
    @SerializedName("listCount")
    public Integer listCount;
    @Expose
    @SerializedName("lists")
    public List<SpeciesGroup> lists;
}
