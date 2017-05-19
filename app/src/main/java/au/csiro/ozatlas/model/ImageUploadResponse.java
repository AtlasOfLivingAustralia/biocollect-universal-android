package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sad038 on 27/4/17.
 */

/**
 * Response from uploading an image
 */
public class ImageUploadResponse {
    @Expose
    @SerializedName("files")
    public Files[] files;

    public class Files {
        @Expose
        @SerializedName("delete_url")
        public String delete_url;
        @Expose
        @SerializedName("isoDate")
        public String isoDate;
        @Expose
        @SerializedName("delete_type")
        public String delete_type;
        @Expose
        @SerializedName("contentType")
        public String contentType;
        @Expose
        @SerializedName("date")
        public String date;
        @Expose
        @SerializedName("url")
        public String url;
        @Expose
        @SerializedName("size")
        public Long size;
        @Expose
        @SerializedName("time")
        public String time;
        @Expose
        @SerializedName("decimalLatitude")
        public String decimalLatitude;
        @Expose
        @SerializedName("thumbnail_url")
        public String thumbnail_url;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("decimalLongitude")
        public String decimalLongitude;
        @Expose
        @SerializedName("attribution")
        public String attribution;
    }
}
