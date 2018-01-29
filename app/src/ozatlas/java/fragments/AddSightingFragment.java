package fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import activity.SingleFragmentActivity;
import adapters.ImageUploadAdapter;
import au.csiro.ozatlas.BuildConfig;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SearchSpeciesAdapter;
import au.csiro.ozatlas.geocode.Constants;
import au.csiro.ozatlas.geocode.FetchAddressIntentService;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.Data;
import au.csiro.ozatlas.model.DraftSpecies;
import au.csiro.ozatlas.model.ExploreAnimal;
import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.model.Outputs;
import au.csiro.ozatlas.model.RealmString;
import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.model.SightingPhoto;
import au.csiro.ozatlas.model.map.CheckMapInfo;
import au.csiro.ozatlas.model.map.MapResponse;
import au.csiro.ozatlas.rest.BieApiService;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.rest.SearchSpeciesSerializer;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import model.OzAtlasLocation;
import model.map.Extent;
import model.map.Geometry;
import model.map.MapModel;
import model.map.Site;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 11/4/17.
 */

public class AddSightingFragment extends BaseMainActivityFragment {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int REQUEST_PLACE_PICKER = 2;
    private static final int REQUEST_IMAGE_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_TAG = 5;
    private static final int REQUEST_OFFLINE_LOCATION = 6;
    private static final int DELAY_IN_MILLIS = 400;
    private static final String DATE_FORMAT = "dd MMMM, yyyy";
    private static final String TIME_FORMAT = "hh:mm a";
    private final int NUMBER_OF_INDIVIDUAL_LIMIT = 100;
    @BindView(R.id.individualSpinner)
    Spinner individualSpinner;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.pickLocation)
    TextView pickLocation;
    @BindView(R.id.speciesDetailLayout)
    LinearLayout speciesDetailLayout;
    @BindView(R.id.speciesURL)
    TextView speciesURL;
    @BindView(R.id.inputLayoutLocation)
    TextInputLayout inputLayoutLocation;
    @BindView(R.id.editLocation)
    EditText editLocation;
    @BindView(R.id.editNote)
    EditText editNote;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.editTags)
    EditText editTags;
    @BindView(R.id.editLatitude)
    EditText editLatitude;
    @BindView(R.id.editLongitude)
    EditText editLongitude;
    @BindView(R.id.editSpeciesName)
    AutoCompleteTextView editSpeciesName;
    @BindView(R.id.confidenceSwitch)
    SwitchCompat confidenceSwitch;
    @BindView(R.id.pickImage)
    TextView pickImage;
    @BindView(R.id.imagePlaceHolder)
    ImageView imagePlaceHolder;
    @BindView(R.id.inputLayoutSpeciesName)
    TextInputLayout inputLayoutSpeciesName;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * The formatted location address.
     */
    private String mAddressOutput;

    private String[] individualSpinnerValue = new String[NUMBER_OF_INDIVIDUAL_LIMIT];
    private ArrayAdapter<String> individualSpinnerAdapter;
    private Calendar now = Calendar.getInstance();
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
    private LocationManager locationManager;
    private BieApiService bieApiService;
    private List<SearchSpecies> species = new ArrayList<>();
    private SearchSpecies selectedSpecies;
    private Double latitude;
    private Double longitude;
    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            hideProgressDialog();
            // Called when a new location is found by the network location provider.
            setCoordinate(location);
            //pickLocation.setText(String.format(Locale.getDefault(), "%.4f, %.4f", location.getLatitude(), location.getLongitude()));
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
    private String siteId;
    private ImageUploadAdapter imageUploadAdapter;
    private String mCurrentPhotoPath;
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

        setTitle(getString(R.string.add_title));

        //hiding the floating action button
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.hideFloatingButton();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mResultReceiver = new AddressResultReceiver(new Handler());

        //setting the date
        time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), TIME_FORMAT).toUpperCase());
        date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), DATE_FORMAT));

        makeIndividualLimit();
        // Create an ArrayAdapter using the string array and a default spinner layout
        individualSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_textview_individual_count, individualSpinnerValue);
        // Specify the layout to use when the list of choices appears
        individualSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        individualSpinner.setAdapter(individualSpinnerAdapter);

        //recycler view setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.zero_dp, R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        imageUploadAdapter = new ImageUploadAdapter(sightingPhotos, getActivity(), sharedPreferences.getUserDisplayName());
        imageUploadAdapter.buttonVisibilityListener = new ImageUploadAdapter.ButtonVisibilityListener() {
            @Override
            public void update() {
                if (imageUploadAdapter.getItemCount() == 0) {
                    pickImage.setVisibility(View.VISIBLE);
                    imagePlaceHolder.setVisibility(View.VISIBLE);
                } else {
                    pickImage.setVisibility(View.GONE);
                    imagePlaceHolder.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(imageUploadAdapter);

        getSightForEdit();

        //species search service
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<List<SearchSpecies>>() {
        }.getType(), new SearchSpeciesSerializer()).create();
        bieApiService = new NetworkClient(getString(R.string.bie_url), gson).getRetrofit().create(BieApiService.class);

        editSpeciesName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSpecies = species.get(position);
                speciesDetailLayout.setVisibility(View.VISIBLE);
                speciesURL.setText(String.format(Locale.getDefault(), "http://bie.ala.org.au/species/%s", selectedSpecies.guid));
                if (BuildConfig.DEBUG) {
                    Toast.makeText(getActivity(), selectedSpecies.highlight, Toast.LENGTH_LONG).show();
                }
            }
        });

        mCompositeDisposable.add(getSearchSpeciesResponseObserver());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Add Sight", TAG);
    }

    @OnClick(R.id.editTags)
    void editTags() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.TAG_SELECTION);
        bundle.putString(getString(R.string.tag_string_parameter), editTags.getText().toString());
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_TAG);
    }

    /**
     * click on editLocation editText
     */
    @OnClick(R.id.editLocation)
    void editLocation() {
        pickLocation();
    }

    @OnClick(R.id.editLatitude)
    void editLatitude() {
        pickLocation();
    }

    @OnClick(R.id.editLongitude)
    void editLongitude() {
        pickLocation();
    }

    /**
     * checking whether the bundle has a Sight Id
     * If it does, the respective Sight is being read from realm
     * finally set the values to View
     */
    private void getSightForEdit() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            ExploreAnimal animal = (ExploreAnimal) getArguments().getSerializable(getString(R.string.species_parameter));
            if (animal == null) {
                setTitle(getString(R.string.edit_title));
                Long id = bundle.getLong(getString(R.string.sight_parameter));
                RealmQuery<AddSight> query = realm.where(AddSight.class).equalTo("realmId", id);
                RealmResults<AddSight> results = query.findAllAsync();
                results.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<AddSight>>() {
                    @Override
                    public void onChange(RealmResults<AddSight> collection, OrderedCollectionChangeSet changeSet) {
                        if (collection.size() > 0) {
                            addSight = collection.first();
                            if (addSight != null)
                                setSightValues();
                        }
                    }
                });
            } else {
                selectedSpecies = new SearchSpecies();
                selectedSpecies.name = animal.name;
                selectedSpecies.kingdom = animal.kingdom;
                selectedSpecies.guid = animal.guid;
                speciesDetailLayout.setVisibility(View.VISIBLE);
                speciesURL.setText(String.format(Locale.getDefault(), "http://bie.ala.org.au/species/%s", animal.guid));
                editSpeciesName.setText(animal.name);
                latitude = bundle.getDouble(getString(R.string.latitude_parameter));
                longitude = bundle.getDouble(getString(R.string.longitude_parameter));
                inputLayoutLocation.setHint(getString(R.string.location_hint));

                Location location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                editLatitude.setText(String.format(Locale.getDefault(), "%.4f", location.getLatitude()));
                editLongitude.setText(String.format(Locale.getDefault(), "%.4f", location.getLongitude()));
                startIntentService(location);
            }
        }
    }

    /**
     * data validation for uploading an Sight
     *
     * @return
     */
    private boolean getValidated() {
        boolean value = true;
        if (editSpeciesName.getText().toString().length() < 1) {
            inputLayoutSpeciesName.setError("Please choose a species");
            value = false;
        }
        if (latitude == null || longitude == null) {
            value = false;
            inputLayoutLocation.setError(getString(R.string.location_missing));
            showSnackBarMessage(getString(R.string.location_missing));
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
            //when the user press the SAVE button
            case R.id.save:
                AtlasManager.hideKeyboard(getActivity());
                if (AtlasManager.isNetworkAvailable(getActivity())) {
                    if (getValidated()) {
                        showProgressDialog();
                        uploadMap(getMapModel());
                    }
                } else {
                    //if there is no network, the sight will be saved in realm as Draft Sight
                    AtlasDialogManager.alertBox(getActivity(), getString(R.string.no_internet_message), getString(R.string.not_internet_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (addSight == null || !addSight.isManaged()) {
                                getAddSightModel();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealm(addSight);
                                    }
                                });
                            } else {
                                realm.beginTransaction();
                                getAddSightModel();
                                realm.commitTransaction();
                            }

                            showSnackBarMessage("Sighting has been saved as Draft");
                            if (getActivity() instanceof SingleFragmentActivity) {
                                getActivity().setResult(RESULT_OK);
                                getActivity().onBackPressed();
                            } else {
                                setDrawerMenuChecked(R.id.nav_draft_sighting);
                                getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new DraftSightingListFragment()).commit();
                            }
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //upload the Sight Object
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
                        handleError(e, 0, "");
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();

                        if (addSight.isManaged()) {
                            realm.beginTransaction();
                            addSight.deleteFromRealm();
                            realm.commitTransaction();
                        }

                        if (getActivity() instanceof SingleFragmentActivity) {
                            getActivity().setResult(RESULT_OK);
                            getActivity().finish();
                        } else {
                            setDrawerMenuChecked(R.id.nav_all_sighting);
                            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SightingListFragment()).commit();
                        }
                    }
                }));
    }

    /**
     * upload photos
     */
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
                            handleError(e, 0, "");
                        }

                        @Override
                        public void onComplete() {
                            imageUploadCount++;
                            if (imageUploadCount < sightingPhotos.size())
                                uploadPhotos();
                            else
                                saveData();
                        }
                    }));
        }
    }

    private MapModel getMapModel() {
        if (latitude != null && longitude != null) {
            MapModel mapModel = new MapModel();
            mapModel.pActivityId = getString(R.string.project_activity_id);
            mapModel.site = new Site();
            mapModel.site.name = "Private site for survey: Individual sighting";
            mapModel.site.visibility = "private";
            mapModel.site.projects = new String[]{getString(R.string.project_id)};
            mapModel.site.extent = new Extent();
            mapModel.site.extent.source = "Point";
            mapModel.site.extent.geometry = new Geometry();
            mapModel.site.extent.geometry.areaKmSq = 0.0;
            mapModel.site.extent.geometry.type = "Point";
            mapModel.site.extent.geometry.coordinates = new Double[2];
            mapModel.site.extent.geometry.coordinates[0] = longitude;
            mapModel.site.extent.geometry.coordinates[1] = latitude;
            mapModel.site.extent.geometry.centre = new Double[2];
            mapModel.site.extent.geometry.centre[0] = longitude;
            mapModel.site.extent.geometry.centre[1] = latitude;

            return mapModel;
        } else {
            showSnackBarMessage(getString(R.string.location_missing));
        }
        return null;
    }

    private void uploadMap(MapModel mapModel) {
        Log.d("MAP_MODEL", new Gson().toJson(mapModel));
        mCompositeDisposable.add(restClient.getService().postMap(mapModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<MapResponse>() {
                    @Override
                    public void onNext(MapResponse mapResponse) {
                        siteId = mapResponse.id;
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        handleError(e, 0, "");
                    }

                    @Override
                    public void onComplete() {
                        if (sightingPhotos.size() > 0) {
                            imageUploadCount = 0;
                            uploadPhotos();
                        } else {
                            //other wise get the unique id to upload a sight
                            saveData();
                        }
                    }
                }));
    }

    /**
     * set the values if the fragment is for Editing a Sight
     */
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

                if (addSight.outputs.get(0).data.notes != null) {
                    editNote.setText(addSight.outputs.get(0).data.notes);
                }

                if (addSight.outputs.get(0).data.tags != null) {
                    String s[] = new String[addSight.outputs.get(0).data.tags.size()];
                    for (int i = 0; i < addSight.outputs.get(0).data.tags.size(); i++) {
                        s[i] = addSight.outputs.get(0).data.tags.get(i).val;
                    }
                    String tags = TextUtils.join(getString(R.string.tag_separator), s);
                    editTags.setText(tags.length() > 0 ? tags + getString(R.string.tag_separator) : "");
                }

                if (addSight.outputs.get(0).data.locationLatitude != null) {
                    if (editLocation.getVisibility() == View.GONE)
                        editLocation.setVisibility(View.VISIBLE);
                    latitude = addSight.outputs.get(0).data.locationLatitude;
                    longitude = addSight.outputs.get(0).data.locationLongitude;
                    inputLayoutLocation.setHint(getString(R.string.location_hint));

                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    editLatitude.setText(String.format(Locale.getDefault(), "%.4f", location.getLatitude()));
                    editLongitude.setText(String.format(Locale.getDefault(), "%.4f", location.getLongitude()));
                    startIntentService(location);
                }

                if (addSight.outputs.get(0).data.sightingPhoto != null) {
                    sightingPhotos.addAll(realm.copyFromRealm(addSight.outputs.get(0).data.sightingPhoto));
                    imageUploadAdapter.notifyDataSetChanged();
                    if (imageUploadAdapter.buttonVisibilityListener != null)
                        imageUploadAdapter.buttonVisibilityListener.update();
                }
            }
        }
    }

    /**
     * preparing a SightModel from the values of the views
     *
     * @return
     */
    private AddSight getAddSightModel() {
        if (addSight == null) {
            addSight = new AddSight();
            Number number = realm.where(AddSight.class).max("realmId");
            if (number == null)
                addSight.realmId = 1L;
            else
                addSight.realmId = number.longValue() + 1;
        }
        addSight.projectStage = "";
        addSight.type = getString(R.string.project_type);
        addSight.projectId = getString(R.string.project_id);
        addSight.siteId = siteId;
        addSight.outputs = new RealmList<>();
        Outputs outputs = new Outputs();
        outputs.name = getString(R.string.project_output_name);
        outputs.checkMapInfo = new CheckMapInfo();
        outputs.checkMapInfo.validation = true;
        outputs.data = new Data();
        outputs.data.recordedBy = sharedPreferences.getUserDisplayName();
        outputs.data.surveyDate = AtlasDateTimeUtils.getFormattedDayTime(date.getText().toString(), DATE_FORMAT, AtlasDateTimeUtils.DEFAULT_DATE_FORMAT);
        outputs.data.surveyStartTime = AtlasDateTimeUtils.getFormattedDayTime(date.getText().toString() + time.getText().toString(), DATE_FORMAT + TIME_FORMAT, AtlasDateTimeUtils.DEFAULT_DATE_FORMAT);
        if (outputs.data.species == null)
            outputs.data.species = new DraftSpecies();
        if (selectedSpecies != null) {
            outputs.data.species.name = selectedSpecies.name;
            outputs.data.species.scientificName = selectedSpecies.kingdom;
            outputs.data.species.guid = selectedSpecies.guid;
        } else if (outputs.data.species.name == null || outputs.data.species.guid == null) {
            outputs.data.species.name = editSpeciesName.getText().toString();
            outputs.data.species.scientificName = outputs.data.species.name;
        }
        //outputs.data.species.commonName = "";
        outputs.data.individualCount = Integer.parseInt((String) individualSpinner.getSelectedItem());
        outputs.data.identificationConfidence = confidenceSwitch.isChecked() ? "Certain" : "Uncertain";
        outputs.data.sightingPhoto = imageUploadAdapter.getSightingPhotos();
        outputs.data.tags = new RealmList<>();
        String tags[] = editTags.getText().toString().split(";");
        for (String string : tags) {
            outputs.data.tags.add(new RealmString(string.trim()));
        }
        outputs.data.locationLatitude = latitude;
        outputs.data.locationLongitude = longitude;
        outputs.data.notes = editNote.getText().toString();
        addSight.outputs.add(outputs);
        //realm.commitTransaction();
        return addSight;
    }

    /**
     * network call for species suggestion
     *
     * @return
     */
    private DisposableObserver<List<SearchSpecies>> getSearchSpeciesResponseObserver() {
        return RxTextView.textChangeEvents(editSpeciesName)
                .debounce(DELAY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .map(new Function<TextViewTextChangeEvent, String>() {
                    @Override
                    public String apply(TextViewTextChangeEvent textViewTextChangeEvent) throws Exception {
                        return textViewTextChangeEvent.text().toString();
                    }
                })
                .filter(s -> s.length() > 1)
                .observeOn(Schedulers.io())
                .flatMap(s -> bieApiService.searchSpecies(s, "taxonomicStatus:accepted"))
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribeWith(new DisposableObserver<List<SearchSpecies>>() {
                    @Override
                    public void onNext(List<SearchSpecies> speciesSearchResponse) {
                        species.clear();
                        species.addAll(speciesSearchResponse);

                        editSpeciesName.setAdapter(new SearchSpeciesAdapter(getActivity(), species));
                        if (species.size() == 0 || (selectedSpecies != null && selectedSpecies.name.equals(editSpeciesName.getText().toString()))) {
                            editSpeciesName.dismissDropDown();
                        } else {
                            editSpeciesName.showDropDown();
                            speciesDetailLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleError(e, 0, "");
                    }

                    @Override
                    public void onComplete() {

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

    @OnClick(R.id.date)
    public void date() {
        (new DatePickerDialog(getActivity(), R.style.DateTimeDialogTheme, onDateSetListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))).show();
    }

    @OnClick(R.id.pickLocation)
    void pickLocation() {
        if (AtlasManager.isNetworkAvailable(getActivity())) {
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
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_LOCATION_FRAGMENT);
            bundle.putBoolean(getString(R.string.is_location_selection_parameter), true);
            Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_OFFLINE_LOCATION);
        }
    }

    @OnClick(R.id.speciesDetailLayout)
    void speciesDetailLayout() {
        startWebViewActivity(speciesURL.getText().toString(), "DraftSpecies Detail", false);
    }

    /**
     * look for hardware GPS location
     */
    private void lookForGPSLocation() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(AddSightingFragment.this);
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
                case REQUEST_IMAGE_CAPTURE:
                    if (mCurrentPhotoPath != null) {
                        SightingPhoto sightingPhoto = new SightingPhoto();
                        FileUtils.galleryAddPic(getActivity(), mCurrentPhotoPath);
                        sightingPhoto.filePath = mCurrentPhotoPath;
                        sightingPhoto.filename = (new File(mCurrentPhotoPath)).getName();
                        sightingPhotos.add(sightingPhoto);
                        imageUploadAdapter.notifyDataSetChanged();
                        mCurrentPhotoPath = null;
                        imageUploadAdapter.buttonVisibilityListener.update();
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    final Uri selectedImageUri = data.getData();
                    sightingPhotos.add(getSightingPhotoWithFileNameAdded(selectedImageUri));
                    imageUploadAdapter.notifyDataSetChanged();
                    imageUploadAdapter.buttonVisibilityListener.update();
                    break;
                case REQUEST_CODE_AUTOCOMPLETE:
                    final Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    setCoordinate(place);
                    break;
                case REQUEST_PLACE_PICKER:
                    final Place place1 = PlacePicker.getPlace(getActivity(), data);
                    setCoordinate(place1);
                    break;
                case REQUEST_TAG:
                    String tagValues = data.getStringExtra(getString(R.string.tag_string_parameter));
                    editTags.setText(tagValues);
                    break;
                case REQUEST_OFFLINE_LOCATION:
                    int id = data.getIntExtra(getString(R.string.location_selection_parameter), -1);
                    if (id != -1) {
                        setLocationLabels();
                        RealmQuery<OzAtlasLocation> query = realm.where(OzAtlasLocation.class).equalTo("id", id);
                        RealmResults<OzAtlasLocation> results = query.findAllAsync();
                        results.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<OzAtlasLocation>>() {
                            @Override
                            public void onChange(RealmResults<OzAtlasLocation> collection, OrderedCollectionChangeSet changeSet) {
                                OzAtlasLocation ozAtlasLocation = collection.first();
                                setLatitudeLongitude(ozAtlasLocation.latitude, ozAtlasLocation.longitude);
                                latitude = ozAtlasLocation.latitude;
                                longitude = ozAtlasLocation.longitude;
                                editLocation.setText(ozAtlasLocation.addressLine);
                            }
                        });
                    }
                    break;
            }
        }
    }

    /**
     * get the filename and the path after attachign an image
     * so that the Sight model can be saved locally
     *
     * @param fileUri
     * @return
     */
    private SightingPhoto getSightingPhotoWithFileNameAdded(Uri fileUri) {
        SightingPhoto sightingPhoto = new SightingPhoto();
        sightingPhoto.filePath = FileUtils.getPath(getActivity(), fileUri);
        sightingPhoto.filename = (FileUtils.getFile(getActivity(), fileUri)).getName();
        return sightingPhoto;
    }

    /**
     * set the coordinate with Place object
     * and update the textview
     *
     * @param place
     */
    private void setCoordinate(Place place) {
        setLocationLabels();
        setLatitudeLongitude(place.getLatLng().latitude, place.getLatLng().longitude);
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
        editLocation.setText(place.getAddress());
    }

    /**
     * set the coordinate with Location object
     * and update the textview
     *
     * @param location
     */
    private void setCoordinate(Location location) {
        setLocationLabels();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        setLatitudeLongitude(latitude, longitude);
        startIntentService(location);
    }

    /**
     * changing the labels after choosing/picking an address
     */
    private void setLocationLabels() {
        pickLocation.setText(R.string.location_change_text);
        inputLayoutLocation.setHint(getString(R.string.location_hint));
    }

    private void setLatitudeLongitude(Double latitude, Double longitude) {
        editLatitude.setText(String.format(Locale.getDefault(), "%.4f", latitude));
        editLongitude.setText(String.format(Locale.getDefault(), "%.4f", longitude));
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

    /**
     * when the user clicks "add image" button
     */
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

    /**
     * method to start the camera
     */
    private void dispatchTakePictureIntent() {
        File f = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.getUriFromFileProvider(getContext(), f));

        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getActivity().grantUriPermission(packageName, FileUtils.getUriFromFileProvider(getContext(), f), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Make a filename for the camera picture
     *
     * @return
     * @throws IOException
     */
    private File setUpPhotoFile() throws IOException {
        File f = FileUtils.createImageFile(getActivity());
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }


    /**
     * open the Gallery
     */
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
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            editLocation.setText(mAddressOutput);
        }
    }
}