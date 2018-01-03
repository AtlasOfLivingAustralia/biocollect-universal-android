package model.track;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import au.csiro.ozatlas.manager.RealmListParcelConverter;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by sad038 on 28/11/17.
 */

@Parcel
@RealmClass
public class SightingEvidenceTable extends RealmObject {
    @Expose
    public String typeOfSign;

    @Expose
    public Double observationLongitude;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    @Expose
    public RealmList<ImageModel> imageOfSign;

    @Expose
    public Species species;

    @Expose
    public Double observationLatitude;

    @Expose
    public String evidenceAgeClass;

    @Expose
    public String ageClassOfAnimal;

    //view
    public String mPhotoPath;
}
