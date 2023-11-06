package fragments.addtrack.map;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Calendar;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.fragments.CustomMapFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeDialogManager;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.Language;
import au.csiro.ozatlas.manager.Utils;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragments.CustomSpinnerAdapter;
import fragments.addtrack.AddTrackFragment;
import fragments.addtrack.BilbyDataManager;
import fragments.addtrack.ValidationCheck;
import io.realm.RealmList;
import model.track.BilbyBlitzData;
import model.track.BilbyLocation;

/**
 * Created by sad038 on 9/10/17.
 */

public class TrackMapFragment extends BaseMainActivityFragment implements ValidationCheck, OnMapReadyCallback, BilbyDataManager {
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String DATE_FORMAT = "dd MMMM, yyyy";
    private static final String TIME_FORMAT = "hh:mm a";
    private final float INITIAL_ZOOM = 19.0f;
    public boolean acquireGPSLocation = true;
    @BindView(R.id.surveySpinner)
    AppCompatSpinner surveySpinner;
    @BindView(R.id.siteSpinner)
    AppCompatSpinner siteSpinner;
    @BindView(R.id.editDate)
    EditText editDate;
    @BindView(R.id.editStartTime)
    EditText editStartTime;
    @BindView(R.id.editEndTime)
    EditText editEndTime;
    @BindView(R.id.inputLayoutDate)
    TextInputLayout inputLayoutDate;
    @BindView(R.id.inputLayoutstartTime)
    TextInputLayout inputLayoutstartTime;
    @BindView(R.id.surveyTextView)
    TextView surveyTextView;
    @BindView(R.id.siteTextView)
    TextView siteTextView;
    @BindView(R.id.gpsMessageTextView)
    TextView gpsMessageTextView;
    @BindView(R.id.inputLayoutEndTime)
    TextInputLayout inputLayoutEndTime;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    /**
     * Date Picker Listener
     */
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.YEAR, year);
            now.set(Calendar.MONTH, month);
            now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editDate.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), DATE_FORMAT));
        }
    };
    private Marker lastMarker;
    private RealmList<BilbyLocation> locations = new RealmList<>();
    private GoogleMap googleMap;
    private CustomMapFragment mapFragment;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;
    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mService.requestLocationUpdates();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };
    private PolylineOptions polylineOptions;
    private BilbyBlitzData bilbyBlitzData;
    private Calendar now = Calendar.getInstance();
    /**
     * Time picker Listener
     */
    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            now.set(Calendar.HOUR_OF_DAY, hourOfDay);
            now.set(Calendar.MINUTE, minute);
            editStartTime.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), TIME_FORMAT).toUpperCase());
        }
    };
    /**
     * Time picker Listener
     */
    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            now.set(Calendar.HOUR_OF_DAY, hourOfDay);
            now.set(Calendar.MINUTE, minute);
            editEndTime.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), TIME_FORMAT).toUpperCase());
        }
    };
    private LocationManager locationManager;
    private String[] surveyTypeEnglishValues;
    private String[] siteTypeEnglishValues;

    @Override
    protected void setLanguageValues(Language language) {
        inputLayoutDate.setHint(localisedString("event_date_hint", R.string.event_date_hint));
        inputLayoutstartTime.setHint(localisedString("event_start_time_hint", R.string.event_start_time_hint));
        inputLayoutEndTime.setHint(localisedString("event_end_time_hint", R.string.event_end_time_hint));
        //gpsMessageTextView.setText(localisedString("number_of_location", R.string.number_of_location));
        surveyTextView.setText(localisedString("survey_type", R.string.survey_type));
        siteTextView.setText(localisedString("site_type", R.string.site_type));

        switch (language) {
            case WARLPIRI:
                surveySpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.survey_type_adithinngithigh), R.layout.item_textview));
                siteSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.site_type_adithinngithigh), R.layout.item_textview));
                break;
            case WARUMUNGU:
                surveySpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.survey_type_warumungu), R.layout.item_textview));
                siteSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.site_type_warumungu), R.layout.item_textview));
                break;
            default:
                surveySpinner.setAdapter(new CustomSpinnerAdapter(getContext(), surveyTypeEnglishValues, R.layout.item_textview));
                siteSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), siteTypeEnglishValues, R.layout.item_textview));
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_map, container, false);
        ButterKnife.bind(this, view);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        myReceiver = new MyReceiver();

        surveyTypeEnglishValues = getResources().getStringArray(R.array.survey_type);
        siteTypeEnglishValues = getResources().getStringArray(R.array.site_type);
        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());
        if (getParentFragment() instanceof AddTrackFragment) {
            acquireGPSLocation = ((AddTrackFragment) getParentFragment()).acquireGPSLocation;
            bilbyBlitzData = ((AddTrackFragment) getParentFragment()).getBilbyBlitzData();
            setBilbyBlitzData();
        }

        lookForGPSSettings();
        return view;
    }

    public void setBilbyBlitzData() {
        if (bilbyBlitzData.surveyDate != null) {
            editDate.setText(AtlasDateTimeUtils.getFormattedDayTime(bilbyBlitzData.surveyDate, DATE_FORMAT));
        } else {
            editDate.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), DATE_FORMAT));
        }

        if (bilbyBlitzData.surveyStartTime != null) {
            editStartTime.setText(bilbyBlitzData.surveyStartTime);
        } else {
            editStartTime.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), TIME_FORMAT).toUpperCase());
        }

        if (bilbyBlitzData.surveyFinishTime != null) {
            editEndTime.setText(bilbyBlitzData.surveyFinishTime);
        }

        if (bilbyBlitzData.tempLocations != null)
            locations = bilbyBlitzData.tempLocations;

        gpsMessageTextView.setText(getString(R.string.number_of_location, locations.size()));

        surveySpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.survey_type), bilbyBlitzData.surveyType));
        siteSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.site_type), bilbyBlitzData.siteChoice));
    }

    private void putCoordinatesInVisibleArea() {
        if (googleMap != null && locations.size() > 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (BilbyLocation location : locations) {
                builder.include(new LatLng(location.latitude, location.longitude));
            }
            LatLngBounds bounds = builder.build();
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getActivity().bindService(new Intent(getActivity(), LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
            //lookForGPSSettings();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            getActivity().unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    /**
     * requesting Location access permission
     */
    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * look for hardware GPS Settings
     */
    public void lookForGPSSettings() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
     * Add Google Map Market on Google Map
     *
     * @param latLng
     */
    private void setGoogleMapView(LatLng latLng) {
        if (lastMarker != null) {
            lastMarker.remove();
        }
        if(googleMap!=null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM));
            lastMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    /**
     * Add Google Map View on Google Map
     *
     * @param location
     */
    private void setGoogleMapView(Location location) {
        setGoogleMapView(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /**
     * Drawing the polyline on Google Map
     *
     * @param location
     */
    private void addPolyLine(Location location) {
        if (polylineOptions != null) {
            polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
            googleMap.addPolyline(polylineOptions);
            putCoordinatesInVisibleArea();
        }
    }

    /**
     * Drawing the polyline on Google Map
     *
     * @param location
     */
    private void addPolyLine(BilbyLocation location) {
        if (polylineOptions != null) {
            polylineOptions.add(new LatLng(location.latitude, location.longitude));
            googleMap.addPolyline(polylineOptions);
            putCoordinatesInVisibleArea();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (CustomMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new CustomMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapLayout, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
    }

    private void setError(TextInputLayout inputLayout, String error) {
        if (isAdded()) {
            inputLayout.setError(error);
        }
    }

    @Override
    public String getValidationMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        if (locations.size() == 0) {
            stringBuilder.append(localisedString("location_missing", R.string.location_missing));
            stringBuilder.append("\n");
        }

        if (TextUtils.isEmpty(editDate.getText())) {
            stringBuilder.append(localisedString("event_date_missing_error", R.string.event_date_missing_error));
            stringBuilder.append("\n");
            setError(inputLayoutDate, localisedString("event_date_missing_error", R.string.event_date_missing_error));
        } else {
            setError(inputLayoutDate, "");
        }

        if (TextUtils.isEmpty(editStartTime.getText())) {
            stringBuilder.append(localisedString("event_time_missing_error", R.string.event_time_missing_error));
            setError(inputLayoutstartTime, localisedString("event_time_missing_error", R.string.event_time_missing_error));
        } else {
            setError(inputLayoutstartTime, "");
        }

        return stringBuilder.toString().trim();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapFragment.setListener(() -> scrollView.requestDisallowInterceptTouchEvent(true));
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        polylineOptions = new PolylineOptions()
                .color(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .width(getResources().getDimension(R.dimen.map_line_width));
        for (BilbyLocation location : locations) {
            addPolyLine(location);
        }
    }

    @OnClick(R.id.editDate)
    void editDate() {
        AtlasDateTimeDialogManager.showDatePicker(getActivity(), onDateSetListener);
    }

    @OnClick(R.id.editStartTime)
    void editStartTime() {
        AtlasDateTimeDialogManager.showTimePicker(getContext(), startTimeSetListener);
    }

    @OnClick(R.id.editEndTime)
    void editEndTime() {
        AtlasDateTimeDialogManager.showTimePicker(getContext(), endTimeSetListener);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                getActivity().bindService(new Intent(getActivity(), LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    /**
     * preparing the Track Model
     */
    @Override
    public void prepareData() {
        if (locations.size() > 0) {
            bilbyBlitzData.locationLatitude = locations.get(0).latitude;
            bilbyBlitzData.locationLongitude = locations.get(0).longitude;
        }
        bilbyBlitzData.surveyType = surveySpinner.getSelectedItemPosition() == 0 ? null : surveyTypeEnglishValues[surveySpinner.getSelectedItemPosition()];
        bilbyBlitzData.siteChoice = siteSpinner.getSelectedItemPosition() == 0 ? null : siteTypeEnglishValues[siteSpinner.getSelectedItemPosition()];
        bilbyBlitzData.surveyDate = AtlasDateTimeUtils.getFormattedDayTime(editDate.getText().toString(), DATE_FORMAT, AtlasDateTimeUtils.DEFAULT_DATE_FORMAT);
        bilbyBlitzData.surveyStartTime = editStartTime.getText().toString() ;
        bilbyBlitzData.surveyFinishTime = editEndTime.getText().toString();
        bilbyBlitzData.tempLocations = locations;
        if (locations.size() == 1) {
            BilbyLocation bilbyLocation = new BilbyLocation(locations.get(0).latitude + 0.00001, locations.get(0).longitude + +0.00001);
            bilbyBlitzData.tempLocations.add(bilbyLocation);
        }
    }

    private double locationRound(double num) {
        return Math.floor(num * 100000) / 100000;
    }

    /*
    If fifth digit is after decimal is different then its different
     */
    private boolean isSameLocation(BilbyLocation location1, BilbyLocation location2) {
        double latitude_diff = Math.abs(locationRound(location1.latitude) - locationRound(location2.latitude));
        double longitude_diff = Math.abs(locationRound(location1.longitude) - locationRound(location2.longitude));
        return latitude_diff == 0 && longitude_diff == 0;
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) {
                Location location;

                location = intent.getParcelableExtra(LocationUpdatesService.LAST_KNOWN_LOCATION);
                if (location != null) {
                    setGoogleMapView(location);
                    //Toast.makeText(getContext(), location.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
                if (location != null && acquireGPSLocation) {
                    BilbyLocation bilbyLocation = new BilbyLocation(location.getLatitude(), location.getLongitude());
                    if (locations.size() == 0 || (locations.size() > 0 && !isSameLocation(locations.get(locations.size() - 1), bilbyLocation))) {
                        locations.add(bilbyLocation);
                        setGoogleMapView(location);
                        addPolyLine(location);
                        gpsMessageTextView.setText(getString(R.string.number_of_location, locations.size()));
                    }
                }
            }
        }
    }

}
