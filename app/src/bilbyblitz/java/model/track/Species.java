package model.track;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import au.csiro.ozatlas.model.SearchSpecies;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class Species extends RealmObject {
    @Expose
    public String guid;

    @Expose
    public String commonName;

    @Expose
    public String outputSpeciesId;

    @Expose
    public String scientificName;

    @Expose
    public String name;

    public Species() {
    }

    public Species(String scientificName, String name) {
        this.scientificName = scientificName;
        this.name = name;
    }

    public Species(SearchSpecies searchSpecies) {
        this.guid = searchSpecies.guid;
        this.commonName = searchSpecies.commonName;
        this.name = searchSpecies.name;
        this.scientificName = searchSpecies.scientificName;
        this.outputSpeciesId = searchSpecies.outputSpeciesId;
    }

}
