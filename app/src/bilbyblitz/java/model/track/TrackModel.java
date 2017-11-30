package model.track;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import au.csiro.ozatlas.manager.RealmListParcelConverter;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class TrackModel extends RealmObject {
    public String mainTheme;

    public String activityId;

    public String siteId;

    public String projectId;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<BilbyBlitzOutputs> outputs;

    public String type;

    public String projectStage;
}
