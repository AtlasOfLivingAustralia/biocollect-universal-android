package model.track;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import au.csiro.ozatlas.manager.RealmListParcelConverter;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class TrackModel extends RealmObject {
    @PrimaryKey
    public Long realmId;
    public Boolean upLoading;

    @Expose
    public String mainTheme = "";

    @Expose
    public String activityId;

    @Expose
    public String siteId;

    @Expose
    public String projectId;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    @Expose
    public RealmList<BilbyBlitzOutput> outputs;

    @Expose
    public String type;

    @Expose
    public String projectStage = "";
}
