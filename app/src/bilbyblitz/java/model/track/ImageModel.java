package model.track;

import org.parceler.Parcel;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class ImageModel extends RealmObject {
    public String licence;

    public String status;

    public String contentType;

    public String url;

    public String formattedSize;

    public String dateTaken;

    public Long filesize;

    public String thumbnailUrl;

    public String name;

    public String filename;

    public String notes;

    public Boolean staged;

    public String documentId;

    public String attribution;

    //view
    public String mPhotoPath;
}
