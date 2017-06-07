package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

/**
 * part of uploading a Sight [AddSight]
 * This is because Realm can not handle primitive Arraylist
 */
public class Tag extends RealmObject {
    @Expose
    @SerializedName("val")
    public String val;

    public Tag() {
    }

    public Tag(String val) {
        this.val = val;
    }
}
