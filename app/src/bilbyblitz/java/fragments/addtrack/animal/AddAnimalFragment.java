package fragments.addtrack.animal;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import au.csiro.ozatlas.BuildConfig;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SearchSpeciesAdapter;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.manager.Utils;
import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.rest.BieApiService;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.rest.SearchSpeciesSerializer;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Case;
import io.realm.RealmResults;
import model.track.SightingEvidenceTable;
import model.track.Species;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 9/10/17.
 */

public class AddAnimalFragment extends BaseMainActivityFragment {
    private static final int REQUEST_IMAGE_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int DELAY_IN_MILLIS = 400;

    @BindView(R.id.editSpeciesName)
    AutoCompleteTextView editSpeciesName;
    @BindView(R.id.whatSeenSpinner)
    AppCompatSpinner whatSeenSpinner;
    @BindView(R.id.howRecentSpinner)
    AppCompatSpinner howRecentSpinner;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.speciesDetailLayout)
    LinearLayout speciesDetailLayout;
    @BindView(R.id.speciesURL)
    TextView speciesURL;
    @BindView(R.id.species_loading_indicator)
    ProgressBar speciesLoadingIndicator;
    @BindView(R.id.editLatitude)
    EditText editLatitude;
    @BindView(R.id.editLongitude)
    EditText editLongitude;
    @BindView(R.id.whatSeenTextView)
    TextView whatSeenTextView;
    @BindView(R.id.recentTextView)
    TextView recentTextView;

    private String mCurrentPhotoPath;
    private List<SearchSpecies> species = new ArrayList<>();
    private SightingEvidenceTable sightingEvidenceTable;
    private BieApiService bieApiService;

    @Override
    protected void setLanguageValues() {
        editSpeciesName.setHint(localisedString("animal", R.string.animal));
        editLatitude.setHint(localisedString("sign_latitude", R.string.sign_latitude));
        editLongitude.setHint(localisedString("sign_longitude", R.string.sign_longitude));
        recentTextView.setText(localisedString("how_recent", R.string.how_recent));
        whatSeenTextView.setText(localisedString("what_you_see", R.string.what_you_see));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_animal, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        //set the localized labels
        setLanguageValues();

        whatSeenSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.what_see_values, R.layout.item_textview));
        whatSeenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sightingEvidenceTable.typeOfSign = (String) whatSeenSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        howRecentSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.how_recent_values, R.layout.item_textview));
        howRecentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sightingEvidenceTable.evidenceAgeClass = (String) howRecentSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //species search service
        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<List<SearchSpecies>>() {
        }.getType(), new SearchSpeciesSerializer()).create();
        bieApiService = new NetworkClient(getString(R.string.bie_url), gson).getRetrofit().create(BieApiService.class);

        editSpeciesName.setOnItemClickListener((parent, view1, position, id) -> {
            sightingEvidenceTable.species = new Species(species.get(position));
            speciesDetailLayout.setVisibility(View.VISIBLE);
            speciesURL.setText(String.format(Locale.getDefault(), "http://bie.ala.org.au/species/%s", sightingEvidenceTable.species.guid));
            if (BuildConfig.DEBUG) {
                Toast.makeText(getActivity(), sightingEvidenceTable.species.commonName, Toast.LENGTH_LONG).show();
            }
        });

        Bundle bundle = getArguments();
        if (bundle == null) {
            sightingEvidenceTable = new SightingEvidenceTable();
        } else {
            sightingEvidenceTable = Parcels.unwrap(bundle.getParcelable(getString(R.string.add_animal_parameter)));
            if (sightingEvidenceTable == null)
                sightingEvidenceTable = new SightingEvidenceTable();
            else
                setValues();
        }

        if(AtlasManager.isNetworkAvailable(getActivity()))
            mCompositeDisposable.add(getSearchSpeciesResponseObserver());
        else
            mCompositeDisposable.add(searchInRealm());

        return view;
    }

    private void setValues() {
        if (sightingEvidenceTable.species != null) {
            editSpeciesName.setText(sightingEvidenceTable.species.name);
            speciesDetailLayout.setVisibility(View.VISIBLE);
            speciesURL.setText(String.format(Locale.getDefault(), "http://bie.ala.org.au/species/%s", sightingEvidenceTable.species.guid));
        }
        whatSeenSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.what_see_values), sightingEvidenceTable.typeOfSign));
        howRecentSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.how_recent_values), sightingEvidenceTable.evidenceAgeClass));
        if (sightingEvidenceTable.mPhotoPath != null) {
            imageView.setImageBitmap(FileUtils.getBitmapFromFilePath(sightingEvidenceTable.mPhotoPath));
        }
        if (sightingEvidenceTable.observationLatitude != null)
            editLatitude.setText(String.valueOf(sightingEvidenceTable.observationLatitude));
        if (sightingEvidenceTable.observationLongitude != null)
            editLongitude.setText(String.valueOf(sightingEvidenceTable.observationLongitude));
    }

    /**
     * network call for species suggestion
     *
     * @return
     */
    private Disposable getSearchSpeciesResponseObserver() {
        return RxTextView.textChangeEvents(editSpeciesName)
                .debounce(DELAY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .map(textViewTextChangeEvent -> textViewTextChangeEvent.text().toString())
                .filter(s -> {
                    boolean call = s.length() > 1;
                    if (call)
                        getActivity().runOnUiThread(() -> speciesLoadingIndicator.setVisibility(View.VISIBLE));
                    return call;
                })
                .observeOn(Schedulers.io())
                .flatMap(s -> bieApiService.searchSpecies(s, "taxonomicStatus:accepted"))
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(this::setSpeciesAdapter);
    }

    private void setSpeciesAdapter(List<SearchSpecies> searchSpecies) {
        speciesLoadingIndicator.setVisibility(View.INVISIBLE);
        species.clear();
        species.addAll(searchSpecies);

        editSpeciesName.setAdapter(new SearchSpeciesAdapter(getActivity(), species));
        if (species.size() == 0 || (sightingEvidenceTable.species != null && sightingEvidenceTable.species.name.equals(editSpeciesName.getText().toString()))) {
            editSpeciesName.dismissDropDown();
        } else {
            editSpeciesName.showDropDown();
            speciesDetailLayout.setVisibility(View.GONE);
        }
    }

    private Disposable searchInRealm() {
        return RxTextView.textChangeEvents(editSpeciesName)
                .debounce(DELAY_IN_MILLIS, TimeUnit.MILLISECONDS) // default Scheduler is Schedulers.computation()
                .map(textViewTextChangeEvent -> textViewTextChangeEvent.text().toString())
                .filter(s -> {
                    boolean call = s.length() > 1;
                    if (call)
                        getActivity().runOnUiThread(() -> speciesLoadingIndicator.setVisibility(View.VISIBLE));
                    return call;
                })
                .observeOn(AndroidSchedulers.mainThread()) // Needed to access Realm data
                .toFlowable(BackpressureStrategy.BUFFER)
                .switchMap(string -> {
                    // Use Async API to move Realm queries off the main thread.
                    // Realm currently doesn't support the standard Schedulers.
                    return realm.where(SearchSpecies.class)
                            .contains("commonName", string, Case.INSENSITIVE)
                            .or()
                            .contains("name", string, Case.INSENSITIVE)
                            .findAllAsync()
                            .asFlowable();
                })
                // Only continue once data is actually loaded
                // RealmObservables will emit the unloaded (empty) list as its first item
                .filter(RealmResults::isLoaded)
                .subscribe(searchSpecies -> setSpeciesAdapter(realm.copyFromRealm(searchSpecies)), throwable -> throwable.printStackTrace());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void prepareSightingEvidenceTableModel() {
        if (!Utils.isNullOrEmpty(editLatitude.getText().toString()))
            sightingEvidenceTable.observationLatitude = Utils.parseDouble(editLatitude.getText().toString());
        if (!Utils.isNullOrEmpty(editLongitude.getText().toString()))
            sightingEvidenceTable.observationLongitude = Utils.parseDouble(editLongitude.getText().toString());
    }

    private boolean isSightingEvidenceTableModelValid() {
        return sightingEvidenceTable.species != null;
    }

    private void prepareData(){
        sightingEvidenceTable.typeOfSign = (String)whatSeenSpinner.getSelectedItem();
        sightingEvidenceTable.evidenceAgeClass = (String)howRecentSpinner.getSelectedItem();
        sightingEvidenceTable.observationLongitude = Utils.parseDouble(editLongitude.getText().toString());
        sightingEvidenceTable.observationLatitude = Utils.parseDouble(editLatitude.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                prepareSightingEvidenceTableModel();
                if (isSightingEvidenceTableModelValid()) {
                    prepareData();
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.add_animal_parameter), Parcels.wrap(sightingEvidenceTable));
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().onBackPressed();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
                        sightingEvidenceTable.mPhotoPath = mCurrentPhotoPath;
                        FileUtils.galleryAddPic(getActivity(), mCurrentPhotoPath);
                        imageView.setImageBitmap(FileUtils.getBitmapFromFilePath(mCurrentPhotoPath));
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    final Uri selectedImageUri = data.getData();
                    mCurrentPhotoPath = FileUtils.getPath(getActivity(), selectedImageUri);
                    sightingEvidenceTable.mPhotoPath = mCurrentPhotoPath;
                    imageView.setImageURI(selectedImageUri);
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

    @OnClick(R.id.addPhotoButton)
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
     * Make a filename for the camera picture
     *
     * @return
     * @throws IOException
     */
    private File setUpPhotoFile() throws IOException {
        mCurrentPhotoPath = null;
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

    /**
     * method to start the camera
     */
    private void dispatchTakePictureIntent() {
        File f = null;
        mCurrentPhotoPath = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.getUriFromFileProvider(getActivity(), f));

        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getActivity().grantUriPermission(packageName, FileUtils.getUriFromFileProvider(getActivity(), f), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


}

