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
public class BilbyBlitzOutputs extends RealmObject {
    public String name;

    public BilbyBlitzData data;

    public String appendTableRows;

    public String outputId;

    public String outputNotCompleted;
}
