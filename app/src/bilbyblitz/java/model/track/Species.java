package model.track;

import org.parceler.Parcel;

import java.io.Serializable;

import au.csiro.ozatlas.model.SearchSpecies;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class Species extends RealmObject {
    public String guid;

    public String commonName;

    public String outputSpeciesId;

    public String scientificName;

    public String name;

    public Species() {
    }

    public Species(SearchSpecies searchSpecies) {
        this.guid = searchSpecies.guid;
        this.commonName = searchSpecies.commonName;
        this.name = searchSpecies.name;
        this.scientificName = searchSpecies.scientificName;
        this.outputSpeciesId = searchSpecies.outputSpeciesId;
    }

}
