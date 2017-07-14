package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sad038 on 23/6/17.
 */

public class ExploreAnimal implements Serializable{
    @Expose
    @SerializedName("guid")
    public String guid;

    @Expose
    @SerializedName("commonName")
    public String commonName;

    public String rank;

    @Expose
    @SerializedName("count")
    public Integer count;

    public String kingdom;

    @Expose
    @SerializedName("family")
    public String family;

    @Expose
    @SerializedName("name")
    public String name;

}
