package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 12/1/18.
 */

@Parcel
@RealmClass
public class KvpValues extends RealmObject {
    @Expose
    public String value;
    @Expose
    public String key;
}
