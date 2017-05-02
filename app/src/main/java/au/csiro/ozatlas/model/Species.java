package au.csiro.ozatlas.model;

import io.realm.RealmObject;

/**
 * Created by sad038 on 2/5/17.
 */

public class Species extends RealmObject {
    public Species() {
    }

    public String guid;

    public String commonName;

    public String outputSpeciesId;

    public String scientificName;

    public String name;
}
