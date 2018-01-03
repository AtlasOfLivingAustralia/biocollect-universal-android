package model.track;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class TrackerGroupImage extends RealmObject {
    @Expose
    public String licence;

    @Expose
    public String status;

    @Expose
    public String contentType;

    @Expose
    public String url;

    @Expose
    public String formattedSize;

    @Expose
    public String dateTaken;

    @Expose
    public String filesize;

    @Expose
    public String thumbnailUrl;

    @Expose
    public String name;

    @Expose
    public String filename;

    @Expose
    public String notes;

    @Expose
    public String staged;

    @Expose
    public String documentId;

    @Expose
    public String attribution;
}
