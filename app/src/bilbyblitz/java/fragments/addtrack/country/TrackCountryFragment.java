package fragments.addtrack.country;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.manager.Utils;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragments.addtrack.AddTrackFragment;
import fragments.addtrack.BilbyDataManager;
import fragments.addtrack.ValidationCheck;
import fragments.setting.Language;
import io.realm.RealmList;
import model.track.BilbyBlitzData;
import model.track.ImageModel;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 9/10/17.
 */

public class TrackCountryFragment extends BaseMainActivityFragment implements ValidationCheck, BilbyDataManager {
    private static final int REQUEST_IMAGE_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    @BindView(R.id.detailHabitatSpinner)
    AppCompatSpinner detailHabitatSpinner;
    @BindView(R.id.habitatSpinner)
    AppCompatSpinner habitatSpinner;
    @BindView(R.id.plotTypeSpinner)
    AppCompatSpinner plotTypeSpinner;
    @BindView(R.id.trackingEventSpinner)
    AppCompatSpinner trackingEventSpinner;
    @BindView(R.id.disturbanceSpinner)
    AppCompatSpinner disturbanceSpinner;
    @BindView(R.id.fireSpinner)
    AppCompatSpinner fireSpinner;
    @BindView(R.id.surfaceTrackingSpinner)
    AppCompatSpinner surfaceTrackingSpinner;
    @BindView(R.id.visibilitySpinner)
    AppCompatSpinner visibilitySpinner;
    @BindView(R.id.groundTypeSpinner)
    AppCompatSpinner groundTypeSpinner;
    @BindView(R.id.addPhotoButton)
    Button addPhotoButton;
    @BindView(R.id.editCountryName)
    EditText editCountryName;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.detailHabitatTextView)
    TextView detailHabitatTextView;
    @BindView(R.id.habitatTextView)
    TextView habitatTextView;
    @BindView(R.id.plotTextView)
    TextView plotTextView;
    @BindView(R.id.trackEventTextView)
    TextView trackEventTextView;
    @BindView(R.id.disturbanceTextView)
    TextView disturbanceTextView;
    @BindView(R.id.groundTextView)
    TextView groundTextView;
    @BindView(R.id.fireTextView)
    TextView fireTextView;
    @BindView(R.id.visibilityTextView)
    TextView visibilityTextView;
    @BindView(R.id.surfaceTextView)
    TextView surfaceTextView;
    @BindView(R.id.inputLayoutCountryName)
    TextInputLayout inputLayoutCountryName;

    private String mCurrentPhotoPath;
    private BilbyBlitzData bilbyBlitzData;

    @Override
    protected void setLanguageValues(Language language) {
        inputLayoutCountryName.setHint(localisedString("country_name", R.string.country_name));
        detailHabitatTextView.setText(localisedString("detailed_habitat", R.string.detailed_habitat));
        habitatTextView.setText(localisedString("habitat", R.string.habitat));
        plotTextView.setText(localisedString("type_of_plot", R.string.type_of_plot));
        trackEventTextView.setText(localisedString("tracking_event_type", R.string.tracking_event_type));
        disturbanceTextView.setText(localisedString("disturbance", R.string.disturbance));
        groundTextView.setText(localisedString("ground_type", R.string.ground_type));
        fireTextView.setText(localisedString("fire", R.string.fire));
        visibilityTextView.setText(localisedString("visibility", R.string.visibility));
        surfaceTextView.setText(localisedString("surface_tracking", R.string.surface_tracking));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_cuontry, container, false);
        //setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

        /*int[] icons = new int[]{R.drawable.breakawayhill, R.drawable.countrytype, R.drawable.drainageline, R.drawable.laterite, R.drawable.rockyrange};
        String[] types = getResources().getStringArray(R.array.dummy_type);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> datum = new HashMap<String, Object>(2);
            datum.put("imageView", icons[i]);
            datum.put("name", types[i]);
            data.add(datum);
        }
        countryTypeSpinner.setAdapter(new SimpleAdapter(getContext(), data, R.layout.row_icon_string, new String[] {"imageView","name"}, new int[] {R.id.imageView, R.id.name}));

        icons = new int[]{R.drawable.little, R.drawable.clear, R.drawable.some_clear};
        types = getResources().getStringArray(R.array.dummy_type);
        data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> datum = new HashMap<String, Object>(2);
            datum.put("imageView", icons[i]);
            datum.put("name", types[i]);
            data.add(datum);
        }
        groundTypeSpinner.setAdapter(new SimpleAdapter(getContext(), data, R.layout.row_icon_string, new String[] {"imageView","name"}, new int[] {R.id.imageView, R.id.name}));
        */

        detailHabitatSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.detailed_habitat_values, R.layout.item_textview));
        /*detailHabitatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO which member
                //bilbyBlitzData.habitatType = (String) detailHabitatSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        habitatSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.habitat_values, R.layout.item_textview));
        /*habitatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bilbyBlitzData.habitatType = (String) habitatSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        plotTypeSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.plot_type, R.layout.item_textview));
        /*plotTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bilbyBlitzData.plotType = (String) plotTypeSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        trackingEventSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.tracking_event_values, R.layout.item_textview));
        /*trackingEventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //todo chaeck
                bilbyBlitzData.surfaceTrackability = (String) trackingEventSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        disturbanceSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.disturbance_values, R.layout.item_textview));
        /*disturbanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sightingEvidenceTable.evidenceAgeClass = (String) disturbanceSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        groundTypeSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.ground_values, R.layout.item_textview));
        /*groundTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sightingEvidenceTable.evidenceAgeClass = (String) groundTypeSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        fireSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.fire_type, R.layout.item_textview));
        /*fireSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sightingEvidenceTable.evidenceAgeClass = (String) fireSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        visibilitySpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.visibility_type, R.layout.item_textview));
        /*visibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sightingEvidenceTable.evidenceAgeClass = (String) visibilitySpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        surfaceTrackingSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.tracking_surface_value, R.layout.item_textview));
        /*surfaceTrackingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sightingEvidenceTable.evidenceAgeClass = (String) surfaceTrackingSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        if (getParentFragment() instanceof AddTrackFragment) {
            bilbyBlitzData = ((AddTrackFragment) getParentFragment()).getBilbyBlitzData();
            setBilbyBlitzData();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TrackCountryFragmentCurrentPhotoPath", mCurrentPhotoPath);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString("TrackCountryFragmentCurrentPhotoPath");
            if (mCurrentPhotoPath != null)
                imageView.setImageBitmap(FileUtils.getBitmapFromFilePath(mCurrentPhotoPath));
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
                        FileUtils.galleryAddPic(getActivity(), mCurrentPhotoPath);
                        imageView.setImageBitmap(FileUtils.getBitmapFromFilePath(mCurrentPhotoPath));
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    final Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        mCurrentPhotoPath = FileUtils.getPath(getActivity(), selectedImageUri);
                        imageView.setImageURI(selectedImageUri);
                    }
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

    @Override
    public String getValidationMessage() {
        return null;
    }

    @Override
    public void prepareData() {
        bilbyBlitzData.countryName = editCountryName.getText().toString();
        bilbyBlitzData.habitatType = habitatSpinner.getSelectedItemPosition() == 0 ? null :(String) habitatSpinner.getSelectedItem();
        bilbyBlitzData.plotType = plotTypeSpinner.getSelectedItemPosition() == 0 ? null :(String) plotTypeSpinner.getSelectedItem();
        bilbyBlitzData.plotSequence = trackingEventSpinner.getSelectedItemPosition() == 0 ? null :(String) trackingEventSpinner.getSelectedItem();
        bilbyBlitzData.disturbance = disturbanceSpinner.getSelectedItemPosition() == 0 ? null :(String) disturbanceSpinner.getSelectedItem();
        bilbyBlitzData.fireSigns = fireSpinner.getSelectedItemPosition() == 0 ? null :(String) fireSpinner.getSelectedItem();
        bilbyBlitzData.trackingSurfaceContinuity = groundTypeSpinner.getSelectedItemPosition() == 0 ? null :(String) groundTypeSpinner.getSelectedItem();
        bilbyBlitzData.visibility = visibilitySpinner.getSelectedItemPosition() == 0 ? null :(String) visibilitySpinner.getSelectedItem();
        bilbyBlitzData.vegetationType = detailHabitatSpinner.getSelectedItemPosition() == 0 ? null :(String) detailHabitatSpinner.getSelectedItem();
        bilbyBlitzData.surfaceTrackability = surfaceTrackingSpinner.getSelectedItemPosition() == 0 ? null :(String) surfaceTrackingSpinner.getSelectedItem();
        if (mCurrentPhotoPath != null) {
            bilbyBlitzData.locationImage = new RealmList<>();
            ImageModel imageModel = new ImageModel();
            imageModel.mPhotoPath = mCurrentPhotoPath;
            bilbyBlitzData.locationImage.add(imageModel);
        }
    }

    @Override
    public void setBilbyBlitzData() {
        editCountryName.setText(bilbyBlitzData.countryName);
        habitatSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.habitat_values), bilbyBlitzData.habitatType), false);
        plotTypeSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.plot_type), bilbyBlitzData.plotType), false);
        trackingEventSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.tracking_event_values), bilbyBlitzData.plotSequence), false);
        disturbanceSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.disturbance_values), bilbyBlitzData.disturbance), false);
        fireSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.fire_type), bilbyBlitzData.fireSigns), false);
        groundTypeSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.ground_values), bilbyBlitzData.trackingSurfaceContinuity), false);
        visibilitySpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.visibility_type), bilbyBlitzData.visibility), false);
        detailHabitatSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.detailed_habitat_values), bilbyBlitzData.vegetationType), false);
        surfaceTrackingSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.tracking_surface_value), bilbyBlitzData.surfaceTrackability), false);
    }
}
