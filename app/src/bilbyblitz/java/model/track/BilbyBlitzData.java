package model.track;

import com.google.gson.annotations.Expose;

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
    @Expose
    public Double locationCentroidLatitude;

    @Expose
    public String visibility;

    @Expose
    public String countryName;

    @Expose
    public String location;

    @Expose
    public String locationAccuracy;

    @Expose
    public String surveyStartTime;

    @Expose
    public String surveyFinishTime;

    @Expose
    public Double locationLongitude;

    @Expose
    public String organisationName;

    @Expose
    public String additionalTrackers;

    @Expose
    public String fireHistory;

    @Expose
    public String siteChoice;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    @Expose
    public RealmList<FoodPlant> foodPlants;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    @Expose
    public RealmList<SightingEvidenceTable> sightingEvidenceTable;

    @Expose
    public String surfaceTrackability;

    @Expose
    public String vegetationType;

    @Expose
    public Double locationLatitude;

    @Expose
    public String trackingSurfaceContinuity;

    @Expose
    public String plotSequence;

    @Expose
    public String disturbance;

    @Expose
    public String recordedBy;

    @Expose
    public String habitatType;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    @Expose
    public RealmList<TrackerGroupImage> trackerGroupImage;

    @Expose
    public Double locationCentroidLongitude;

    @Expose
    public String surveyDate;

    @Expose
    public String surveyType;

    //temporary saving
    @ParcelPropertyConverter(RealmListParcelConverter.class)
    public RealmList<BilbyLocation> tempLocations;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    @Expose
    public RealmList<ImageModel> locationImage;
}
