package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sad038 on 23/6/17.
 */

public class ExploreGroup {
    public String level;

    @Expose
    @SerializedName("count")
    public Integer count;

    @Expose
    @SerializedName("speciesCount")
    public Integer speciesCount;

    @Expose
    @SerializedName("name")
    public String name;
}
