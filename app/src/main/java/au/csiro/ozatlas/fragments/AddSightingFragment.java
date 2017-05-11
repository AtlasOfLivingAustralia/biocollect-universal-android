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
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.google.gson.JsonObject;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.SingleFragmentActivity;
import au.csiro.ozatlas.adapter.ImageUploadAdapter;
import au.csiro.ozatlas.adapter.SearchSpeciesAdapter;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.Data;
import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.model.Outputs;
import au.csiro.ozatlas.model.SightingPhoto;
import au.csiro.ozatlas.model.Species;
import au.csiro.ozatlas.model.SpeciesSearchResponse;
import au.csiro.ozatlas.model.Tag;
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
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Response;

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
    @BindView(R.id.inputLayoutLocation)
    TextInputLayout inputLayoutLocation;
    @BindView(R.id.editLocation)
    EditText editLocation;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.editSpeciesName)
    AutoCompleteTextView editSpeciesName;
    @BindView(R.id.confidenceSwitch)
    Switch confidenceSwitch;
    @BindView(R.id.inputLayoutSpeciesName)
    TextInputLayout inputLayoutSpeciesName;

    private String[] individualSpinnerValue = new String[NUMBER_OF_INDIVIDUAL_LIMIT];
    private ArrayAdapter<String> individualSpinnerAdapter;
    private ArrayAdapter<String> tagsSpinnerAdapter;
    private Calendar now = Calendar.getInstance();
    private LocationManager locationManager;
    private BieApiService bieApiService;
    private List<String> tagList;
    private List<SpeciesSearchResponse.Species> species = new ArrayList<>();
    private SpeciesSearchResponse.Species selectedSpecies;
    private Double latitude;
    private Double longitude;
    private String outputSpeciesId;

    private ImageUploadAdapter imageUploadAdapter;
    //private Uri fileUri;
    private String mCurrentPhotoPath;
    //private ArrayList<Uri> paths = new ArrayList<>();
    private RealmList<SightingPhoto> sightingPhotos = new RealmList<>();
    private int imageUploadCount;
    private Realm realm;
    private AddSight addSight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sight, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        realm = Realm.getDefaultInstance();

        //from bundle
        getSightForEdit();

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
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.hideFloatingButton();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        makeIndividualLimit();
        // Create an ArrayAdapter using the string array and a default spinner layout
        individualSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_textview, individualSpinnerValue);
        // Specify the layout to use when the list of choices appears
        individualSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        individualSpinner.setAdapter(individualSpinnerAdapter);

        //setting the date
        time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), TIME_FORMAT).toUpperCase());
        date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), DATE_FORMAT));

        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.zero_dp, R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        imageUploadAdapter = new ImageUploadAdapter(sightingPhotos, getActivity());
        recyclerView.setAdapter(imageUploadAdapter);

        mCompositeDisposable.add(getFileReadObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        Log.d("", value);
                        tagList = createTagLists(value);
                        tagsSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_tags, tagList);
                        tagsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        identificationTagSpinner.setAdapter(tagsSpinnerAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackBarMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (addSight != null) {
                            setSightValues();
                        }
                    }
                }));

        mCompositeDisposable.add(getSearchSpeciesResponseObserver());

        return view;
    }

    private void getSightForEdit() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Long id = bundle.getLong(getString(R.string.sight_parameter));
            addSight = realm.where(AddSight.class).equalTo("realmId", id).findFirst();
        }
    }

    private boolean getValidated() {
        boolean value = true;
        if (selectedSpecies == null) {
            inputLayoutSpeciesName.setError("Please choose a species");
            value = false;
        }
        if (latitude == null || longitude == null) {
            value = false;
            showSnackBarMessage("Please Add a location");
        }
        return value;
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
                AtlasManager.hideKeyboard(getActivity());
                if (AtlasManager.isNetworkAvailable(getActivity())) {
                    if (getValidated()) {
                        showProgressDialog();
                        if (sightingPhotos.size() > 0) {
                            imageUploadCount = 0;
                            uploadPhotos();
                        } else {
                            getGUID();
                        }
                    }
                } else {
                    AtlasDialogManager.alertBoxForSetting(getActivity(), getString(R.string.no_internet_message), getString(R.string.not_internet_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAddSightModel();

                            showSnackBarMessage("Sighting has been saved as Draft");
                            if (getActivity() instanceof SingleFragmentActivity) {
                                getActivity().setResult(RESULT_OK);
                                getActivity().onBackPressed();
                            } else {
                                getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new DraftSightingListFragment()).commit();
                            }
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getGUID() {
        mCompositeDisposable.add(restClient.getService().getGUID()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject value) {
                        if (value.has("outputSpeciesId")) {
                            outputSpeciesId = value.getAsJsonPrimitive("outputSpeciesId").getAsString();
                            saveData();
                        } else {
                            hideProgressDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackBarMessage(e.getMessage());
                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void saveData() {
        mCompositeDisposable.add(restClient.getService().postSightings(getString(R.string.project_activity_id), getAddSightModel())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(Response<Void> value) {
                        showSnackBarMessage("Sighting has been saved");
                        Log.d("", "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showSnackBarMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();

                        realm.beginTransaction();
                        addSight.deleteFromRealm();
                        realm.commitTransaction();

                        if (getActivity() instanceof SingleFragmentActivity) {
                            getActivity().setResult(RESULT_OK);
                            getActivity().onBackPressed();
                        } else
                            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SightingListFragment()).commit();
                    }
                }));
    }

    private void uploadPhotos() {
        if (imageUploadCount < sightingPhotos.size()) {
            mCompositeDisposable.add(restClient.getService().uploadPhoto(FileUtils.getMultipart(sightingPhotos.get(imageUploadCount).filePath))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ImageUploadResponse>() {
                        @Override
                        public void onNext(ImageUploadResponse value) {
                            sightingPhotos.get(imageUploadCount).thumbnailUrl = value.files[0].thumbnail_url;
                            sightingPhotos.get(imageUploadCount).url = value.files[0].url;
                            sightingPhotos.get(imageUploadCount).contentType = value.files[0].contentType;
                            sightingPhotos.get(imageUploadCount).staged = true;
                            Log.d("", value.files[0].thumbnail_url);
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            showSnackBarMessage(e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            imageUploadCount++;
                            if (imageUploadCount < sightingPhotos.size())
                                uploadPhotos();
                            else
                                getGUID();
                        }
                    }));
        }
    }

    private void setSightValues() {
        if (addSight.outputs != null && addSight.outputs.size() > 0) {
            if (addSight.outputs.get(0).data != null) {
                //setting the date
                time.setText(AtlasDateTimeUtils.getFormattedDayTime(addSight.outputs.get(0).data.surveyStartTime, TIME_FORMAT).toUpperCase());
                date.setText(AtlasDateTimeUtils.getFormattedDayTime(addSight.outputs.get(0).data.surveyDate, DATE_FORMAT).toUpperCase());

                individualSpinner.setSelection(addSight.outputs.get(0).data.individualCount - 1, false);
                confidenceSwitch.setChecked(addSight.outputs.get(0).data.identificationConfidence.equals("Certain"));
                if (addSight.outputs.get(0).data.species != null) {
                    editSpeciesName.setText(addSight.outputs.get(0).data.species.name);
                }
                if (addSight.outputs.get(0).data.tags != null) {
                    for (int i = 0; i < tagList.size(); i++) {
                        if (tagList.get(i).equals(addSight.outputs.get(0).data.tags.get(0).val)) {
                            identificationTagSpinner.setSelection(i, false);
                            break;
                        }
                    }
                }
                if (addSight.outputs.get(0).data.locationLatitude != null)
                    editLocation.setText(String.format(Locale.getDefault(), "%.3f, %.3f", addSight.outputs.get(0).data.locationLatitude, addSight.outputs.get(0).data.locationLongitude));

                if (addSight.outputs.get(0).data.sightingPhoto != null) {
                    sightingPhotos.addAll(addSight.outputs.get(0).data.sightingPhoto);
                    imageUploadAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private AddSight getAddSightModel() {
        realm.beginTransaction();
        if (addSight == null) {
            addSight = realm.createObject(AddSight.class, realm.where(AddSight.class).count() + 1);
            // increment index
            //addSight.realmId = ;
        }
        addSight.projectStage = "";
        addSight.type = getString(R.string.project_type);
        addSight.projectId = getString(R.string.project_id);
        /*addSight.activityId="";
        addSight.mainTheme = "";
        addSight.siteId = "";*/
        addSight.outputs = new RealmList<>();
        Outputs outputs = new Outputs();
        outputs.name = getString(R.string.project_output_name);
        /*outputs.outputId = "";
        outputs.outputNotCompleted = "";*/
        outputs.data = new Data();
        outputs.data.recordedBy = sharedPreferences.getUserDisplayName();
        outputs.data.surveyDate = AtlasDateTimeUtils.getFormattedDayTime(date.getText().toString(), DATE_FORMAT, AtlasDateTimeUtils.DEFAULT_DATE_FORMAT);
        outputs.data.surveyStartTime = AtlasDateTimeUtils.getFormattedDayTime(date.getText().toString() + time.getText().toString(), DATE_FORMAT + TIME_FORMAT, AtlasDateTimeUtils.DEFAULT_DATE_FORMAT);
        outputs.data.species = new Species();
        outputs.data.species.outputSpeciesId = outputSpeciesId;
        if (selectedSpecies != null) {
            outputs.data.species.name = selectedSpecies.name;
            outputs.data.species.scientificName = selectedSpecies.kingdom;
        }
        //outputs.data.species.commonName = "";
        outputs.data.individualCount = Integer.parseInt((String) individualSpinner.getSelectedItem());
        outputs.data.identificationConfidence = confidenceSwitch.isChecked() ? "Certain" : "Uncertain";
        outputs.data.sightingPhoto = imageUploadAdapter.getSightingPhotos();
        outputs.data.tags = new RealmList<>();
        outputs.data.tags.add(new Tag(tagsSpinnerAdapter.getItem(identificationTagSpinner.getSelectedItemPosition())));
        outputs.data.locationLatitude = latitude;
        outputs.data.locationLongitude = longitude;
        addSight.outputs.add(outputs);
        realm.commitTransaction();
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
                        showSnackBarMessage(e.getMessage());
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
                return Observable.just(FileUtils.readAsset("tags.txt", getActivity()));
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
            AtlasDialogManager.alertBoxForSetting(getActivity(), "Your Device's GPS or Network is Disable", "Location Provider Status", "Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    dialog.cancel();
                }
            });
        }
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
                    if (mCurrentPhotoPath != null) {
                        SightingPhoto sightingPhoto = new SightingPhoto();
                        FileUtils.galleryAddPic(getActivity(), mCurrentPhotoPath);
                        sightingPhoto.filePath = mCurrentPhotoPath;
                        sightingPhoto.filename = (new File(mCurrentPhotoPath)).getName();
                        sightingPhotos.add(sightingPhoto);
                        imageUploadAdapter.notifyDataSetChanged();
                        mCurrentPhotoPath = null;
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    final Uri selectedImageUri = data.getData();
                    sightingPhotos.add(getSightingPhotoWithFileNameAdded(selectedImageUri));
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

    private SightingPhoto getSightingPhotoWithFileNameAdded(Uri fileUri) {
        SightingPhoto sightingPhoto = new SightingPhoto();
        sightingPhoto.filePath = FileUtils.getPath(getActivity(), fileUri);
        sightingPhoto.filename = (FileUtils.getFile(getActivity(), fileUri)).getName();
        return sightingPhoto;
    }

    private void setCoordinate(Place place) {
        if (inputLayoutLocation.getVisibility() == View.GONE)
            inputLayoutLocation.setVisibility(View.VISIBLE);
        pickLocation.setText(R.string.location_change_text);
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
        editLocation.setText(String.format(Locale.getDefault(), "%.3f, %.3f", place.getLatLng().latitude, place.getLatLng().longitude));
    }

    private void setCoordinate(Location location) {
        if (inputLayoutLocation.getVisibility() == View.GONE)
            inputLayoutLocation.setVisibility(View.VISIBLE);
        pickLocation.setText(R.string.location_change_text);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        editLocation.setText(String.format(Locale.getDefault(), "%.3f, %.3f", location.getLatitude(), location.getLongitude()));
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
        File f = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUriFromFileProvider(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File setUpPhotoFile() throws IOException {
        File f = FileUtils.createImageFile(getActivity());
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private Uri getUriFromFileProvider(File file){
        return FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
    }
    /*private Uri getOutputMediaFileUri() {
        try {
            //return Uri.fromFile(getOutputMediaFile());
            return FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", FileUtils.getOutputMediaFile());
        } catch (Exception ex) {
            Log.d(TAG, "Error getOutputMediaFileUri:" + ex);
        }
        return null;
    }*/

    private void openGalleryLocal() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}