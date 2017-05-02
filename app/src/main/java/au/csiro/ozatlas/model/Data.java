package au.csiro.ozatlas.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

public class Data extends RealmObject {
    public Data() {
    }

    public RealmList<Tag> tags;

    public Double locationLatitude;

    public Species species;

    public String surveyStartTime;

    public String recordedBy;

    public Integer individualCount;

    public Double locationLongitude;

    public String identificationConfidence;

    public RealmList<SightingPhoto> sightingPhoto;

    public String surveyDate;
}
