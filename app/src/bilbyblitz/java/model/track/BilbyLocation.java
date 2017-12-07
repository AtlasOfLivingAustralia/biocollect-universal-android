package model.track;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 6/12/17.
 */

@Parcel
@RealmClass
public class BilbyLocation extends RealmObject{
    public Double latitude;
    public Double longitude;

    public BilbyLocation(){}
    public BilbyLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
