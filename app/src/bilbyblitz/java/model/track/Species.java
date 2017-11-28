package model.track;

import java.io.Serializable;

import au.csiro.ozatlas.model.SearchSpecies;

/**
 * Created by sad038 on 28/11/17.
 */

public class Species implements Serializable{
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
