package au.csiro.ozatlas.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.ImageUploadAdapter;
import au.csiro.ozatlas.adapter.SearchSpeciesAdapter;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.model.SpeciesSearchResponse;
import au.csiro.ozatlas.model.post.AddSight;
import au.csiro.ozatlas.model.post.Data;
import au.csiro.ozatlas.model.post.Outputs;
import au.csiro.ozatlas.model.post.Species;
import au.csiro.ozatlas.rest.BieApiService;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.rest.SearchSpeciesSerializer;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 11/4/17.
 */

public class AddSightingFragment extends BaseFragment {
    final String TAG = "AddSightingFragment";

    private final int NUMBER_OF_INDIVIDUAL_LIMIT = 100;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int REQUEST_PLACE_PICKER = 2;

    private static final int REQUEST_IMAGE_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    private static final int DELAY_IN_MILLIS = 400;
    private static final String DATE_FORMAT = "dd MMMM, yyyy";
    private static final String TIME_FORMAT = "hh:mm a";

    @BindView(R.id.individualSpinner)
    Spinner individualSpinner;
    @BindView(R.id.identificationTagSpinner)
    Spinner identificationTagSpinner;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.pickLocation)
    TextView pickLocation;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.editSpeciesName)
    AutoCompleteTextView editSpeciesName;
    @BindView(R.id.confidenceSwitch)
    SwitchCompat confidenceSwitch;

    private String[] individualSpinnerValue = new String[NUMBER_OF_INDIVIDUAL_LIMIT];
    private ArrayAdapter<String> individualSpinnerAdapter;
    private ArrayAdapter<String> tagsSpinnerAdapter;
    private Calendar now = Calendar.getInstance();
    private LocationManager locationManager;
    private BieApiService bieApiService;
    private List<SpeciesSearchResponse.Species> species = new ArrayList<>();
    private SpeciesSearchResponse.Species selectedSpecies;
    private Double latitude;
    private Double longitude;

    private ImageUploadAdapter imageUploadAdapter;
    private Uri fileUri;
    private ArrayList<Uri> paths = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sight, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        //species search service
        Gson gson = new GsonBuilder().registerTypeAdapter(SpeciesSearchResponse.class, new SearchSpeciesSerializer()).create();
        bieApiService = new NetworkClient(getString(R.string.bie_url), gson).getRetrofit().create(BieApiService.class);

        editSpeciesName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSpecies = species.get(position);
                if (AtlasManager.isTesting) {
                    Toast.makeText(getActivity(), selectedSpecies.highlight, Toast.LENGTH_LONG).show();
                }
            }
        });

        //hiding the floating action button
        floatingActionButtonListener.hideFloatingButton();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        makeIndividualLimit();
        // Create an ArrayAdapter using the string array and a default spinner layout
        individualSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, individualSpinnerValue);
        // Specify the layout to use when the list of choices appears
        individualSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        individualSpinner.setAdapter(individualSpinnerAdapter);

        //setting the date
        time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), TIME_FORMAT).toUpperCase());
        date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), DATE_FORMAT));

        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        imageUploadAdapter = new ImageUploadAdapter(paths);
        recyclerView.setAdapter(imageUploadAdapter);

        mCompositeDisposable.add(getFileReadObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        Log.d("", value);
                        tagsSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, createTagLists(value));
                        tagsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        identificationTagSpinner.setAdapter(tagsSpinnerAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
        mCompositeDisposable.add(getSearchSpeciesResponseObserver());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (AtlasManager.isTesting) {
                    showToast("SAVE Clicked");
                }
                showProgressDialog();
                mCompositeDisposable.add(restClient.getService().postSightings(getString(R.string.project_id), getAddSightModel())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Void>() {
                            @Override
                            public void onNext(Void value) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                hideProgressDialog();
                            }

                            @Override
                            public void onComplete() {
                                hideProgressDialog();
                            }
                        }));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AddSight getAddSightModel() {
        AddSight addSight = new AddSight();
        addSight.projectId = getString(R.string.project_id);
        addSight.outputs = new ArrayList<>();
        Outputs outputs = new Outputs();
        outputs.data = new Data();
        outputs.data.surveyDate = AtlasDateTimeUtils.getFormattedDayTime(date.getText().toString(), DATE_FORMAT, AtlasDateTimeUtils.DEFAULT_DATE_FORMAT);
        outputs.data.surveyStartTime = AtlasDateTimeUtils.getFormattedDayTime(time.getText().toString(), TIME_FORMAT, AtlasDateTimeUtils.DEFAULT_TIME_FORMAT);
        //// TODO: 24/4/17 add other data
        outputs.data.species = new Species();
        //// TODO: 24/4/17 add species info
        outputs.data.individualCount = Integer.parseInt((String) individualSpinner.getSelectedItem());
        outputs.data.identificationConfidence = confidenceSwitch.isChecked() ? "Certain" : "Uncertain";
        outputs.data.sightingPhoto = new ArrayList<>();
        //// TODO: 24/4/17 upload pic info
        outputs.data.tags = new ArrayList<>();
        outputs.data.tags.add(tagsSpinnerAdapter.getItem(identificationTagSpinner.getSelectedItemPosition()));
        outputs.data.locationLatitude = latitude;
        outputs.data.locationLongitude = longitude;
        addSight.outputs.add(outputs);
        return addSight;
    }

    private DisposableObserver<SpeciesSearchResponse> getSearchSpeciesResponseObserver() {
        return RxTextView.textChangeEvents(editSpeciesName)
                .debounce(DELAY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .map(new Function<TextViewTextChangeEvent, String>() {
                    @Override
                    public String apply(TextViewTextChangeEvent textViewTextChangeEvent) throws Exception {
                        return textViewTextChangeEvent.text().toString();
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.length() > 1;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<SpeciesSearchResponse>>() {
                    @Override
                    public ObservableSource<SpeciesSearchResponse> apply(String s) throws Exception {
                        return bieApiService.searchSpecies(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribeWith(new DisposableObserver<SpeciesSearchResponse>() {
                    @Override
                    public void onNext(SpeciesSearchResponse speciesSearchResponse) {
                        species.clear();
                        species.addAll(speciesSearchResponse.results);

                        editSpeciesName.setAdapter(new SearchSpeciesAdapter(getActivity(), species));
                        if (species.size() == 0 || (selectedSpecies != null && selectedSpecies.name.equals(editSpeciesName.getText().toString()))) {
                            editSpeciesName.dismissDropDown();
                        } else {
                            editSpeciesName.showDropDown();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            hideProgressDialog();
            // Called when a new location is found by the network location provider.
            setCoordinate(location);
            //pickLocation.setText(String.format(Locale.getDefault(), "%.3f, %.3f", location.getLatitude(), location.getLongitude()));
            // Remove the listener you previously added
            locationManager.removeUpdates(locationListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    /**
     * @param json to make the string list for keys
     * @return
     */
    private List<String> createTagLists(String json) {
        List<String> tags = new ArrayList<>();
        Set<String> set = new HashSet<>();

        try {
            JSONObject jObject = new JSONObject(json);
            Iterator<?> keys = jObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                if (!set.contains(value)) {
                    tags.add(value);
                    set.add(value);
                }
                tags.add(value.concat(" - ").concat(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tags;
    }

    /**
     * Observable to read the tag.txt file
     *
     * @return
     */
    private Observable<String> getFileReadObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(readAsset("tags.txt"));
            }
        });
    }

    /**
     * making individual numbers from 1 to NUMBER_OF_INDIVIDUAL_LIMIT
     */
    private void makeIndividualLimit() {
        for (int i = 1; i <= NUMBER_OF_INDIVIDUAL_LIMIT; i++) {
            individualSpinnerValue[i - 1] = String.valueOf(i);
        }
    }

    @OnClick(R.id.time)
    public void time() {
        (new TimePickerDialog(getActivity(), R.style.DateTimeDialogTheme, timeSetListener, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false)).show();
    }

    /**
     * Time picker Listener
     */
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            now.set(Calendar.HOUR_OF_DAY, hourOfDay);
            now.set(Calendar.MINUTE, minute);
            time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), TIME_FORMAT).toUpperCase());
        }
    };

    @OnClick(R.id.date)
    public void date() {
        (new DatePickerDialog(getActivity(), R.style.DateTimeDialogTheme, onDateSetListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))).show();
    }

    /**
     * Date Picker Listener
     */
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            now.set(Calendar.YEAR, year);
            now.set(Calendar.MONTH, month);
            now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), DATE_FORMAT));
        }
    };

    /**
     * Reads the text of an asset. Should not be run on the UI thread.
     *
     * @param path The path to the asset.
     * @return The plain text of the asset
     */
    public String readAsset(String path) {
        String contents = "";
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = getActivity().getAssets().open(path);
            reader = new BufferedReader(new InputStreamReader(is));
            contents = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                contents += '\n' + line;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return contents;
    }

    @OnClick(R.id.pickLocation)
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

    private void lookForGPSLocation() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(AddSightingFragment.this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            marshMallowPermission.requestPermissionForLocation();
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showProgressDialog();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            alertBoxForSetting();
        }
    }

    /*----------Method to create an AlertBox ------------- */
    protected void alertBoxForSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your Device's GPS or Network is Disable")
                .setCancelable(false)
                .setTitle("Location Provider Status")
                .setPositiveButton("Setting",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

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
                case REQUEST_IMAGE_CAPTURE:
                    paths.add(fileUri);
                    imageUploadAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_IMAGE_GALLERY:
                    final Uri selectedImageUri = data.getData();
                    paths.add(selectedImageUri);
                    imageUploadAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_AUTOCOMPLETE:
                    final Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    setCoordinate(place);
                    break;
                case REQUEST_PLACE_PICKER:
                    final Place place1 = PlacePicker.getPlace(getActivity(), data);
                    setCoordinate(place1);
                    break;
            }
        }
    }

    private void setCoordinate(Place place) {
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
        pickLocation.setText(String.format(Locale.getDefault(), "%.3f, %.3f", place.getLatLng().latitude, place.getLatLng().longitude));
    }

    private void setCoordinate(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        pickLocation.setText(String.format(Locale.getDefault(), "%.3f, %.3f", location.getLatitude(), location.getLongitude()));
    }

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
            case MarshMallowPermission.CAMERA_PERMISSION_REQUEST_CODE:
            case MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    //todo permission denied, boo! Disable the functionality that depends on this permission.
                }
                break;
        }
    }

    @OnClick(R.id.pickImage)
    void pickImage() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
        if (!marshMallowPermission.isPermissionGrantedForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage();
        } else {
            if (!marshMallowPermission.isPermissionGrantedForCamera()) {
                marshMallowPermission.requestPermissionForCamera();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DateTimeDialogTheme);
                builder.setItems(R.array.image_upload, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            dispatchTakePictureIntent();
                        } else if (which == 1) {
                            openGalleryLocal();
                        }
                    }
                }).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        if (fileUri != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private Uri getOutputMediaFileUri() {
        try {
            //return Uri.fromFile(getOutputMediaFile());
            return FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", getOutputMediaFile());
        } catch (Exception ex) {
            Log.d(TAG, "Error getOutputMediaFileUri:" + ex);
        }
        return null;
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "OSSApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("OSSApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    private void openGalleryLocal() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_IMAGE_GALLERY);
    }
}