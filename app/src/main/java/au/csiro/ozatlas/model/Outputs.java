package au.csiro.ozatlas.model;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

public class Outputs extends RealmObject {
    public Outputs() {
    }

    public String name;

    public Data data;

    public String outputId;

    public String outputNotCompleted;
}
