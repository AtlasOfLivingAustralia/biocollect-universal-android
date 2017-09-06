package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sad038 on 25/5/17.
 */

public class Coverage {
    @Expose
    public String fid;

    @Expose
    public String mvs;

    //@Expose
    //public String[] elect;

    @Expose
    public String precision;

    //@Expose
    //public String[] other;

    //@Expose
    //public String[] centre;

    //@Expose
    //public String[] state;

    @Expose
    public String pid;

    @Expose
    public String layerName;

    @Expose
    public String datum;

    @Expose
    @SerializedName("type")
    public String type;

    //@Expose
    //public String[] ibra;

    @Expose
    public String decimalLatitude;

    @Expose
    public String areaKmSq;

    //@Expose
    //public String[] nrm;

    @Expose
    public String bbox;

    @Expose
    public String mvg;

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    public String decimalLongitude;

    @Expose
    public String locality;

    @Expose
    public String radius;

    //@Expose
    //public String[] lga;

    @Expose
    public String uncertainty;

    //@Expose
    //public String[][][] coordinates;

    //@Expose
    //public String[] cmz;

    @Expose
    public String aream2;

}
