package au.csiro.ozatlas.listener;

import com.google.android.gms.location.places.Place;

/**
 * Created by sad038 on 12/4/17.
 */

public interface PlaceUpdateListener {
    void updatePlace(Place place);
}
