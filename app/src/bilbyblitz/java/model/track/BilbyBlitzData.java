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
public class BilbyBlitzData extends RealmObject{
    public String locationCentroidLatitude;

    public String visibility;

    public String location;

    public String locationAccuracy;

    public String surveyStartTime;

    public Double locationLongitude;

    public String organisationName;

    public String fireSigns;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<SightingEvidenceTable> sightingEvidenceTable;

    public String surfaceTrackability;

    public Double locationLatitude;

    public String trackingSurfaceContinuity;

    public String plotSequence;

    public String recordedBy;

    public String plotType;

    public String habitatType;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<TrackerGroupImage> trackerGroupImage;

    public String locationCentroidLongitude;

    public String surveyDate;

    //public RealmList<String> locationImage;
}
