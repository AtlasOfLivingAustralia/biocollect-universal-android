package au.csiro.ozatlas.model;

import java.util.List;

import au.csiro.ozatlas.model.SightingPhoto;
import io.realm.RealmObject;

/**
 * Created by sad038 on 24/4/17.
 */

public class AddSight extends RealmObject {
    public String mainTheme;

    public String activityId;

    public String siteId;

    public String projectId;

    public List<Outputs> outputs;

    public String type;

    public String projectStage;

    public class Outputs extends RealmObject{
        public String name;

        public Data data;

        public String outputId;

        public String outputNotCompleted;
    }

    public class Data extends RealmObject{
        public List<String> tags;

        public Double locationLatitude;

        public Species species;

        public String surveyStartTime;

        public String recordedBy;

        public Integer individualCount;

        public Double locationLongitude;

        public String identificationConfidence;

        public List<SightingPhoto> sightingPhoto;

        public String surveyDate;
    }

    public class Species extends RealmObject{
        public String guid;

        public String commonName;

        public String outputSpeciesId;

        public String scientificName;

        public String name;
    }
}
