package au.csiro.ozatlas.model;

/**
 * Created by sad038 on 27/4/17.
 */

public class ImageUploadResponse {
    public Files[] files;

    public class Files {
        public String delete_url;

        public String isoDate;

        public String delete_type;

        public String contentType;

        public String date;

        public String url;

        public String size;

        public String time;

        public String decimalLatitude;

        public String thumbnail_url;

        public String name;

        public String decimalLongitude;

        public String attribution;
    }
}
