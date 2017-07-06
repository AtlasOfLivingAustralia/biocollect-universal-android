package fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import geocode.Constants;
import geocode.FetchAddressIntentService;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 21/6/17.
 */

public class ExploreSpeciesFragment extends BaseMainActivityFragment implements OnMapReadyCallback {
    private final float INITIAL_ZOOM = 12.2f;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final int ADD_SIGHT_REQUEST_CODE = 2;
    private final String TAG = "ExploreSpeciesFragment";

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    private LatLng centerLatLng;

    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.editRadius)
    EditText editRadius;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_species, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.explore_species_title));
        hideFloatingButton();
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        showSnackBarFromTop("Zoom out/pan to expand/move the target area.");
        return view;
    }

    @OnClick(R.id.nextButton)
    void nextButton() {
        showSnackBarFromTop("Zoom out/pan to expand/move the target area.");
        /*if (centerLatLng != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.SPECIES_GROUP_FRAGMENT);
            bundle.putDouble(getString(R.string.latitude_parameter), centerLatLng.latitude);
            bundle.putDouble(getString(R.string.longitude_parameter), centerLatLng.longitude);
            bundle.putDouble(getString(R.string.radius_parameter), Double.parseDouble(editRadius.getText().toString().replace("km", "")));

            Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, ADD_SIGHT_REQUEST_CODE);
        } else {
            showSnackBarMessage(getString(R.string.location_missing));
        }*/
    }

    @OnClick(R.id.address)
    void address() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    /**
     * getting last location from fused location service
     */
    private void getLastLocation() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(ExploreSpeciesFragment.this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            marshMallowPermission.requestPermissionForLocation();
            return;
        }

        this.googleMap.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            startIntentService(location);
                            setGoogleMapMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });
    }

    private void setGoogleMapMarker(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                ExploreSpeciesFragment.this.googleMap.clear();
                Projection projection = ExploreSpeciesFragment.this.googleMap.getProjection();
                centerLatLng = projection.getVisibleRegion().latLngBounds.getCenter();
                ExploreSpeciesFragment.this.googleMap.addMarker(new MarkerOptions().position(centerLatLng));
                Location location = new Location("");
                location.setLatitude(centerLatLng.latitude);
                location.setLongitude(centerLatLng.longitude);
                startIntentService(location);

                LatLng bottomLeft = projection.getVisibleRegion().latLngBounds.southwest;
                LatLng topRight = projection.getVisibleRegion().latLngBounds.northeast;

                LatLng middleLeft = new LatLng(centerLatLng.latitude, bottomLeft.longitude);
                LatLng middleTop = new LatLng(topRight.latitude, centerLatLng.longitude);
                float distanceBetweenMiddleLeftAndCentre = getBoundary(centerLatLng, middleLeft);
                float distanceBetweenMiddleTopAndCentre = getBoundary(centerLatLng, middleTop);
                if (distanceBetweenMiddleLeftAndCentre > distanceBetweenMiddleTopAndCentre) {
                    updateMap(distanceBetweenMiddleTopAndCentre);
                } else {
                    updateMap(distanceBetweenMiddleLeftAndCentre);
                }

                //float boundary = getBoundary(centerLatLng, topLeft);

            }
        });
        getLastLocation();
    }

    private void updateMap(double distance) {
        drawCircle(centerLatLng.latitude, centerLatLng.longitude, distance * .97);
        editRadius.setText(String.format(Locale.getDefault(), "%.2f km", distance / 1000));
    }

    private void drawCircle(double lat, double lng, double radius) {
        if (googleMap != null) {
            googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(lat, lng))
                    .radius(radius)
                    .strokeColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                    .strokeWidth(getResources().getDimension(R.dimen.map_circle_stroke_width))
                    .fillColor(ContextCompat.getColor(getActivity(), R.color.map_circle_background)));
        }
    }

    private float getBoundary(LatLng centerLatLng, LatLng topLeft) {
        Location loc1 = new Location("");
        Location loc2 = new Location("");
        loc1.setLatitude(centerLatLng.latitude);
        loc2.setLatitude(topLeft.latitude);
        loc1.setLongitude(centerLatLng.longitude);
        loc2.setLongitude(topLeft.longitude);
        return loc1.distanceTo(loc2);
    }

    /**
     * Marshmellow permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MarshMallowPermission.LOCATION_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                } else {
                    //todo permission denied, boo! Disable the functionality that depends on this permission.
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                address.setText(place.getAddress());
                setGoogleMapMarker(place.getLatLng());

                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == ADD_SIGHT_REQUEST_CODE && resultCode == RESULT_OK) {
            setDrawerMenuChecked(R.id.nav_all_sighting);
            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SightingListFragment()).commit();
        }
    }


    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(Location location) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            address.setText(resultData.getString(Constants.RESULT_DATA_KEY));
        }
    }
}
