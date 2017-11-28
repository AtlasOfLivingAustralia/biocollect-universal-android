package model.track;

import java.io.Serializable;

import au.csiro.ozatlas.model.SearchSpecies;

/**
 * Created by sad038 on 28/11/17.
 */

public class SightingEvidenceTable implements Serializable{
    public String typeOfSign;

    public Double observationLongitude;

    public ImageOfSign[] imageOfSign;

    public Species species;

    public Double observationLatitude;

    public String evidenceAgeClass;
}
