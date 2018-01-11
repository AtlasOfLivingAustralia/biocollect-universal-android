package fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.geocode.Constants;
import au.csiro.ozatlas.geocode.FetchAddressIntentService;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.model.DraftSpecies;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import model.OzAtlasLocation;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 21/8/17.
 */

public class OfflineInformationFragment extends BaseMainActivityFragment {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int REQUEST_PLACE_PICKER = 2;

    @BindView(R.id.add_species)
    TextView addSpecies;
    @BindView(R.id.available_species)
    TextView availableSpecies;
    @BindView(R.id.clear_species)
    TextView clearSpecies;
    @BindView(R.id.add_location)
    TextView addLocation;
    @BindView(R.id.available_location)
    TextView availableLocations;
    @BindView(R.id.clear_location)
    TextView clearLocations;

    private Realm realm;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * The formatted location address.
     */
    private OzAtlasLocation ozAtlasLocation;
    private LocationManager locationManager;
    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            hideProgressDialog();
            // Remove the listener you previously added
            locationManager.removeUpdates(locationListener);
            ozAtlasLocation = new OzAtlasLocation();
            ozAtlasLocation.latitude = location.getLatitude();
            ozAtlasLocation.longitude = location.getLongitude();
            startIntentService(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_information, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.offline_information_title));

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.hideFloatingButton();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mResultReceiver = new AddressResultReceiver(new Handler());

        return view;
    }

    @OnClick(R.id.add_species)
    void addSpecies() {

    }

    @OnClick(R.id.available_species)
    void availableSpecies() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_SPECIES_FRAGMENT);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.clear_species)
    void clearData() {
        AtlasDialogManager.alertBox(getContext(), getString(R.string.clear_data_confirmation), getString(R.string.clear_data), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(DraftSpecies.class);
                        showSnackBarMessage(getString(R.string.successful_message));
                    }
                });
            }
        });
    }

    @OnClick(R.id.add_location)
    void addLocation() {
        pickLocation();
    }

    @OnClick(R.id.available_location)
    void availableLocations() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_LOCATION_FRAGMENT);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.clear_location)
    void clearLocation() {
        AtlasDialogManager.alertBox(getContext(), getString(R.string.clear_location_confirmation), getString(R.string.clear_data), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(OzAtlasLocation.class);
                        showSnackBarMessage(getString(R.string.successful_message));
                    }
                });
            }
        });
    }

    void pickLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DateTimeDialogTheme);
        builder//.setTitle(R.string.select_strategy)
                .setItems(R.array.location_strategies, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            lookForGPSLocation();
                        } else if (which == 1) {
                            openMapToPickLocation();
                        } else if (which == 2) {
                            openAutocompleteActivity();
                        }
                    }
                }).show();
    }

    /**
     * look for hardware GPS location
     */
    private void lookForGPSLocation() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(OfflineInformationFragment.this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            marshMallowPermission.requestPermissionForLocation();
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showProgressDialog();
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            AtlasDialogManager.alertBox(getActivity(), "Your Device's GPS or Network is Disable", "Location Provider Status", "Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    dialog.cancel();
                }
            });
        }
    }


    /**
     * open Google Map to select a location.
     */
    private void openMapToPickLocation() {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getActivity(), "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * it provides a texteditor for writing the address and google gives the
     * address suggestion to select from
     */
    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_AUTOCOMPLETE:
                    final Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    saveLocation(place);
                    break;
                case REQUEST_PLACE_PICKER:
                    final Place place1 = PlacePicker.getPlace(getActivity(), data);
                    saveLocation(place1);
                    break;
            }
        }
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
                    lookForGPSLocation();
                } else {
                    //todo permission denied, boo! Disable the functionality that depends on this permission.
                }
                break;
        }
    }

    private void saveLocation(Place place) {
        final OzAtlasLocation location = new OzAtlasLocation();
        location.latitude = place.getLatLng().latitude;
        location.longitude = place.getLatLng().longitude;
        location.addressLine = place.getAddress().toString();
        location.id = getNextId();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(location);
                showSnackBarMessage(getString(R.string.successful_message));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Offline Information", TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }

    private int getNextId() {
        // increment index
        Number currentIdNum = realm.where(OzAtlasLocation.class).max("id");
        int nextId;
        if (currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
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
    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            if (resultCode == Constants.SUCCESS_RESULT)
                ozAtlasLocation.addressLine = resultData.getString(Constants.RESULT_DATA_KEY);
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ozAtlasLocation.id = getNextId();
                    realm.copyToRealm(ozAtlasLocation);
                    showSnackBarMessage(getString(R.string.successful_message));
                }
            });
        }
    }
}
