package au.csiro.ozatlas.model;

import java.util.List;

/**
 * Created by sad038 on 18/4/17.
 */

public class SpeciesSearchResponse {
    public Integer totalRecords;
    public List<Species> results;

    public class Species{
        public String id;
        public String guid;
        public String name;
        public String kingdom;
        public String highlight;
    }
}
