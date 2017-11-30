package model.track;

import org.parceler.Parcel;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class TrackerGroupImage extends RealmObject {
    public String licence;

    public String status;

    public String contentType;

    public String url;

    public String formattedSize;

    public String dateTaken;

    public String filesize;

    public String thumbnailUrl;

    public String name;

    public String filename;

    public String notes;

    public String staged;

    public String documentId;

    public String attribution;
}
