package model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sad038 on 23/8/17.
 */

public class OzAtlasLocation extends RealmObject{
    @PrimaryKey
    public Integer id;

    public Double latitude;
    public Double longitude;
    public String addressLine;
}
