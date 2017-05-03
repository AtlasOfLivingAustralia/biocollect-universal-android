package au.csiro.ozatlas.model;

import android.net.Uri;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

public class SightingPhoto extends RealmObject{
    public SightingPhoto() {
    }

    public String dateTaken;

    public String licence;

    public String name;

    public String thumbnailUrl;

    public String filename;

    public String contentType;

    public Boolean staged;

    public String notes;

    public String url;

    public String attribution;

    public int licensePosition;

    //Not part of Json
    public String filePath;
}

