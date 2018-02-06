package fragments.addtrack.country;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.Language;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.manager.Utils;
import au.csiro.ozatlas.model.RealmString;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragments.CustomSpinnerAdapter;
import fragments.addtrack.AddTrackFragment;
import fragments.addtrack.BilbyDataManager;
import fragments.addtrack.ValidationCheck;
import io.realm.RealmList;
import language.LanguageManager;
import model.track.BilbyBlitzData;
import model.track.ImageModel;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 9/10/17.
 */

public class TrackCountryFragment extends BaseMainActivityFragment implements ValidationCheck, BilbyDataManager {
    private static final int REQUEST_IMAGE_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_FOOD_PLANT = 1;

    @BindView(R.id.countryTypeSpinner)
    AppCompatSpinner countryTypeSpinner;
    @BindView(R.id.vegetationSpinner)
    AppCompatSpinner vegetationSpinner;
    @BindView(R.id.disturbanceSpinner)
    AppCompatSpinner disturbanceSpinner;
    @BindView(R.id.clearGroundSpinner)
    AppCompatSpinner clearGroundSpinner;
    @BindView(R.id.weatherSpinner)
    AppCompatSpinner weatherSpinner;
    @BindView(R.id.groundTypeSpinner)
    AppCompatSpinner groundTypeSpinner;
    @BindView(R.id.addPhotoButton)
    Button addPhotoButton;
    @BindView(R.id.editCountryName)
    EditText editCountryName;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.countryTypeTextView)
    TextView countryTypeTextView;
    @BindView(R.id.vegetationTextView)
    TextView vegetationTextView;
    @BindView(R.id.disturbanceTextView)
    TextView disturbanceTextView;
    @BindView(R.id.groundTextView)
    TextView groundTextView;
    @BindView(R.id.clearGroundTextView)
    TextView clearGroundTextView;
    @BindView(R.id.weatherTextView)
    TextView weatherTextView;
    @BindView(R.id.inputLayoutCountryName)
    TextInputLayout inputLayoutCountryName;
    @BindView(R.id.fireSpinner)
    AppCompatSpinner fireSpinner;
    @BindView(R.id.fireTextView)
    TextView fireTextView;
    @BindView(R.id.foodPlantSelection)
    TextView foodPlantSelection;

    private String mCurrentPhotoPath;
    private BilbyBlitzData bilbyBlitzData;
    private String[] countryTypeEnglishValues;
    private String[] vegetationEnglishValues;
    private String[] disturbanceEnglishvalues;
    private String[] groundEnglishValues;
    private String[] clearGroundEnglishValues;
    private String[] weatherEnglishValues;
    private String[] fireEnglishValues;
    private String[] foodPlantEnglishValues;
    private String[] foodPlantAboriginalValues;

    @Override
    protected void setLanguageValues(Language language) {
        inputLayoutCountryName.setHint(localisedString("country_name", R.string.country_name));
        countryTypeTextView.setText(localisedString("country_type", R.string.country_type));
        vegetationTextView.setText(localisedString("vegetation", R.string.vegetation));
        disturbanceTextView.setText(localisedString("disturbance", R.string.disturbance));
        groundTextView.setText(localisedString("ground_type", R.string.ground_type));
        clearGroundTextView.setText(localisedString("how_clear_ground", R.string.how_clear_ground));
        weatherTextView.setText(localisedString("weather_tracking", R.string.weather_tracking));
        fireTextView.setText(localisedString("fire", R.string.fire));

        switch (language) {
            case WARLPIRI:
                countryTypeSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.country_type_values_adithinngithigh), R.layout.item_textview));
                vegetationSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.vegetation_type_adithinngithigh), R.layout.item_textview));
                disturbanceSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.disturbance_values_adithinngithigh), R.layout.item_textview));
                groundTypeSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.ground_values_adithinngithigh), R.layout.item_textview));
                clearGroundSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.clear_ground_type_adithinngithigh), R.layout.item_textview));
                weatherSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.weather_value_adithinngithigh), R.layout.item_textview));
                fireSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.fire_type_adithinngithigh), R.layout.item_textview));
                break;
            default:
                countryTypeSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), countryTypeEnglishValues, R.layout.item_textview));
                vegetationSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), vegetationEnglishValues, R.layout.item_textview));
                disturbanceSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), disturbanceEnglishvalues, R.layout.item_textview));
                groundTypeSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), groundEnglishValues, R.layout.item_textview));
                clearGroundSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), clearGroundEnglishValues, R.layout.item_textview));
                weatherSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), weatherEnglishValues, R.layout.item_textview));
                fireSpinner.setAdapter(new CustomSpinnerAdapter(getContext(), fireEnglishValues, R.layout.item_textview));
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_cuontry, container, false);
        ButterKnife.bind(this, view);

        countryTypeEnglishValues = getResources().getStringArray(R.array.country_type_values);
        vegetationEnglishValues = getResources().getStringArray(R.array.vegetation_type);
        disturbanceEnglishvalues = getResources().getStringArray(R.array.disturbance_values);
        groundEnglishValues = getResources().getStringArray(R.array.ground_values);
        clearGroundEnglishValues = getResources().getStringArray(R.array.clear_ground_type);
        weatherEnglishValues = getResources().getStringArray(R.array.weather_value);
        fireEnglishValues = getResources().getStringArray(R.array.fire_type);
        foodPlantEnglishValues = getResources().getStringArray(R.array.food_plant_type);
        foodPlantAboriginalValues = getResources().getStringArray(R.array.food_plant_type_adithinngithigh);
        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

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
                setThumbnail(imageView, mCurrentPhotoPath);
        }
    }

    @OnClick(R.id.foodPlantLayout)
    void foodPlantLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.FOOD_PLANT);
        bundle.putString(getString(R.string.food_plant_parameter), foodPlantSelection.getText().toString());
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_FOOD_PLANT);
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
                        setThumbnail(imageView, mCurrentPhotoPath);
                        //imageView.setImageBitmap(FileUtils.getBitmapFromFilePath(mCurrentPhotoPath));
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    final Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        mCurrentPhotoPath = FileUtils.getPath(getActivity(), selectedImageUri);
                        setThumbnail(imageView, mCurrentPhotoPath);
                        //imageView.setImageURI(selectedImageUri);
                    }
                    break;
                case REQUEST_FOOD_PLANT:
                    foodPlantSelection.setText(data.getStringExtra(getString(R.string.food_plant_parameter)));
                    break;
            }
        }
    }

    private void setThumbnail(ImageView imageView, String path){
        Bitmap bitmap = FileUtils.getBitmapFromFilePath(path);
        if(bitmap!=null) {
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            imageView.setImageBitmap(scaled);
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
        if (!foodPlantSelection.getText().equals(localisedString("no_food_plant", R.string.no_food_plant))) {
            String tags[] = foodPlantSelection.getText().toString().split(";");
            if (tags.length > 0) {
                bilbyBlitzData.foodPlants = new RealmList<>();
                for (String string : tags) {
                    bilbyBlitzData.foodPlants.add(new RealmString(LanguageManager.getEnglishValue(string.trim())));
                }
            }
        }
        bilbyBlitzData.habitatType = countryTypeSpinner.getSelectedItemPosition() == 0 ? null : countryTypeEnglishValues[countryTypeSpinner.getSelectedItemPosition()];
        bilbyBlitzData.vegetationType = vegetationSpinner.getSelectedItemPosition() == 0 ? null : vegetationEnglishValues[vegetationSpinner.getSelectedItemPosition()];
        bilbyBlitzData.fireHistory = fireSpinner.getSelectedItemPosition() == 0 ? null : fireEnglishValues[fireSpinner.getSelectedItemPosition()];
        bilbyBlitzData.trackingSurfaceContinuity = clearGroundSpinner.getSelectedItemPosition() == 0 ? null : clearGroundEnglishValues[clearGroundSpinner.getSelectedItemPosition()];
        bilbyBlitzData.disturbance = disturbanceSpinner.getSelectedItemPosition() == 0 ? null : disturbanceEnglishvalues[disturbanceSpinner.getSelectedItemPosition()];
        bilbyBlitzData.surfaceTrackability = groundTypeSpinner.getSelectedItemPosition() == 0 ? null : groundEnglishValues[groundTypeSpinner.getSelectedItemPosition()];
        bilbyBlitzData.visibility = weatherSpinner.getSelectedItemPosition() == 0 ? null : weatherEnglishValues[weatherSpinner.getSelectedItemPosition()];
        if (mCurrentPhotoPath != null) {
            bilbyBlitzData.locationImage = new RealmList<>();
            ImageModel imageModel = new ImageModel();
            imageModel.mPhotoPath = mCurrentPhotoPath;
            bilbyBlitzData.locationImage.add(imageModel);
        }
    }

    @Override
    public void setBilbyBlitzData() {
        if(bilbyBlitzData.locationImage!=null && bilbyBlitzData.locationImage.size()>0){
            imageView.setImageBitmap(FileUtils.getBitmapFromFilePath(bilbyBlitzData.locationImage.get(0).mPhotoPath));
        }
        editCountryName.setText(bilbyBlitzData.countryName);
        if (bilbyBlitzData.foodPlants != null) {
            String s[] = new String[bilbyBlitzData.foodPlants.size()];
            for (int i = 0; i < bilbyBlitzData.foodPlants.size(); i++) {
                s[i] = localisedString(bilbyBlitzData.foodPlants.get(i).val);
            }
            String tags = TextUtils.join(getString(R.string.food_plant_separator), s);
            foodPlantSelection.setText(tags.length() > 0 ? tags + getString(R.string.food_plant_separator) : "");
        }
        countryTypeSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.country_type_values), bilbyBlitzData.habitatType), false);
        vegetationSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.vegetation_type), bilbyBlitzData.vegetationType), false);
        fireSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.fire_type), bilbyBlitzData.fireHistory), false);
        clearGroundSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.clear_ground_type), bilbyBlitzData.trackingSurfaceContinuity), false);
        disturbanceSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.disturbance_values), bilbyBlitzData.disturbance), false);
        groundTypeSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.ground_values), bilbyBlitzData.surfaceTrackability), false);
        weatherSpinner.setSelection(Utils.stringSearchInArray(getResources().getStringArray(R.array.weather_value), bilbyBlitzData.visibility), false);
    }
}
