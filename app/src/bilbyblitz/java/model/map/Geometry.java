package model.map;

import com.google.gson.annotations.Expose;

/**
 * Created by sad038 on 7/12/17.
 */

public class Geometry {
    @Expose
    public Double areaKmSq;

    @Expose
    public String type;

    @Expose
    public Double[][] coordinates;
}
