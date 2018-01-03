package model.track;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 3/1/18.
 */

@Parcel
@RealmClass
public class CheckMapInfo extends RealmObject{
    public Boolean validation;
}
