package model.track;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 2/5/17.
 */

@Parcel
@RealmClass
public class FoodPlant extends RealmObject {
    @Expose
    @SerializedName("val")
    public String val;

    public FoodPlant() {
    }

    public FoodPlant(String val) {
        this.val = val;
    }
}
