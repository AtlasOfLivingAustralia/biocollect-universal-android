package au.csiro.ozatlas.model;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

public class Tag extends RealmObject {
    public Tag() {
    }

    public Tag(String val) {
        this.val = val;
    }

    public String val;
}
