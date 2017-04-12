package au.csiro.ozatlas.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.MainActivity;
import au.csiro.ozatlas.adapter.PlaceAutocompleteAdapter;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.listener.PlaceUpdateListener;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceChooseFragment extends BaseFragment implements GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "PlaceChooseFragment";
    private static final int REQUEST_PLACE_PICKER = 1;

    @BindView(R.id.autocompleteTextView)
    AutoCompleteTextView autocompleteTextView;
    @BindView(R.id.coordinateLayout)
    CoordinatorLayout coordinateLayout;

    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceUpdateListener placeUpdateListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            placeUpdateListener = (MainActivity) context;
        }
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            placeUpdateListener.updatePlace(place);
            getFragmentManager().popBackStack();
            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // Check if no view has focus:
            View view1 = getActivity().getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }

            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_choose, container, false);
        ButterKnife.bind(this, view);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();

        // Register a listener that receives callbacks when a suggestion has been selected
        autocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, null);
        autocompleteTextView.setAdapter(mAdapter);
        return view;
    }

    @OnClick(R.id.placePickerButton)
    void placePickerButton() {
        //// TODO: 1/05/2016 marshmallow permission check
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            showSnackBarMessage(coordinateLayout, "Google Play Services is not available.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
                /* User has picked a place, extract data.
                   Data is extracted from the returned intent by retrieving a Place object from
                   the PlacePicker.
                 */
            final Place place = PlacePicker.getPlace(data, getActivity());
            placeUpdateListener.updatePlace(place);
            getFragmentManager().popBackStack();
        }
    }

    @OnClick(R.id.gpsLocationButton)
    void gpsLocationButton() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
        if (!marshMallowPermission.isPermissionGrantedForLocation()) {
            marshMallowPermission.requestPermissionForLocation();
        } else {
            getGPSLocation();
        }
    }

    void getGPSLocation() {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MarshMallowPermission.LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getGPSLocation();
                        }
                    }, 100);
                } else {
                    //todo permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showSnackBarMessage(coordinateLayout, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode());
    }
}
