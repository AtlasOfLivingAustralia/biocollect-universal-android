package model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by sad038 on 6/6/17.
 */

public class Survey implements Serializable{
    public String startDate;

    public Visibility visibility;

    public String commentsAllowed;

    public String[] typeConservation;

    public String selectedDocument;

    public String projectId;

    public String version;

    public String datasetContactEmail;

    public String pActivityFormName;

    public String id;

    public String[] environmentalFeatures;

    public String datasetContactPhone;

    public String dataSharingLicense;

    public String description;

    @Expose
    public String name;

    public String projectActivityId;

    public String methodAbstract;

    public String[] typeResearch;

    public String authorGivenNames;

    public String published;

    public String methodName;

    public String[] sites;

    public String datasetContactAddress;

    public String publicAccess;

    public Species species;

    public String status;

    public String[] typeSeo;

    public String[] documents;

    public Alert alert;

    public String[] typeThreat;

    public String environmentalFeaturesSuggest;

    public String datasetContactRole;

    public String authorAffiliation;

    public String[] typeFor;

    public String usageGuide;

    public String[] relatedDatasets;

    public String datasetContactDetails;

    public String authorSurname;

    public String[] submissionRecords;

    public String attribution;

    public String datasetContactName;

    public String restrictRecordToSites;

    public class Alert implements Serializable{
        public String[] allSpecies;

        public String[] emailAddresses;
    }

    public class Species implements Serializable{
        public SingleSpecies singleSpecies;

        public String type;

        public String speciesDisplayFormat;
    }

    public class SingleSpecies implements Serializable{
        public String guid;

        public String commonName;

        public String scientificName;

        public String outputSpeciesId;

        public String name;
    }

    public class Visibility implements Serializable{
        public String embargoOption;

        //public null embargoForDays;

        //public null embargoUntil;

        //public null version;
    }
}
