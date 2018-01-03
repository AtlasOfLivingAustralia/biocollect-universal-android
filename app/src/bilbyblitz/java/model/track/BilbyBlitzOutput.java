package model.track;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class BilbyBlitzOutput extends RealmObject {
    @Expose
    public Boolean selectFromSitesOnly;

    @Expose
    public String name;

    @Expose
    public BilbyBlitzData data;

    @Expose
    public String appendTableRows;

    @Expose
    public String outputId;

    @Expose
    public String outputNotCompleted;

    @Expose
    public CheckMapInfo checkMapInfo;
}
