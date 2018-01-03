package model.track;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 6/12/17.
 */

@Parcel
@RealmClass
public class BilbyLocation extends RealmObject{
    @Expose
    public Double latitude;
    @Expose
    public Double longitude;

    public BilbyLocation(){}
    public BilbyLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
