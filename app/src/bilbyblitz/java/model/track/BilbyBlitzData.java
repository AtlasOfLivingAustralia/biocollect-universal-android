package model.track;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import au.csiro.ozatlas.manager.RealmListParcelConverter;
import au.csiro.ozatlas.model.ImageUploadResponse;
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
    public Double locationCentroidLatitude;

    public String visibility;

    public String countryName;

    public String location;

    public String locationAccuracy;

    public String surveyStartTime;

    public String surveyFinishTime;

    public Double locationLongitude;

    public String organisationName;

    public String additionalTrackers;

    public String fireHistory;

    public String siteChoice;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<FoodPlant> foodPlants;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<SightingEvidenceTable> sightingEvidenceTable;

    public String surfaceTrackability;

    public String vegetationType;

    public Double locationLatitude;

    public String trackingSurfaceContinuity;

    public String plotSequence;

    public String disturbance;

    public String recordedBy;

    public String plotType;

    public String habitatType;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<TrackerGroupImage> trackerGroupImage;

    public Double locationCentroidLongitude;

    public String surveyDate;

    public String surveyType;

    //temporary saving
    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<BilbyLocation> tempLocations;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<ImageModel> locationImage;
}
