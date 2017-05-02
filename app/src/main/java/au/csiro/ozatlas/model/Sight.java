package au.csiro.ozatlas.model;

/**
 * Created by sad038 on 13/4/17.
 */

public class Sight {
    public String embargoed;

    public String status;

    public String lastUpdated;

    public String projectType;

    public String projectId;

    public String type;

    public String activityId;

    public String showCrud;

    public String activityOwnerName;

    public String siteId;

    public String thumbnailUrl;

    public String userId;

    public String name;

    public String projectActivityId;

    public Records[] records;

    public String projectName;

    public class Records {

        public String eventDate;

        public Multimedia[] multimedia;

        public String name;

        public String eventTime;

        public String occurrenceID;

        public String[] coordinates;
    }

    public class Multimedia {
        public String title;

        public String imageId;

        public String rights;

        public String rightsHolder;

        public String license;

        public String type;

        public String identifier;

        public String documentId;

        public String creator;

    }
}
