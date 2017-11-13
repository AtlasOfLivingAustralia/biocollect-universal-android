package fragments.trackers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.MarshMallowPermission;
import au.csiro.ozatlas.model.SightingPhoto;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragments.ValidationCheck;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 19/9/17.
 */

public class TrackersFragment extends BaseMainActivityFragment implements ValidationCheck {
    private static final int REQUEST_IMAGE_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    @BindView(R.id.editOrganisationName)
    EditText editOrganisationName;
    @BindView(R.id.editLeadTracker)
    EditText editLeadTracker;
    @BindView(R.id.editOtherTracker)
    EditText editOtherTracker;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.inputLayoutOrganisationName)
    TextInputLayout inputLayoutOrganisationName;
    @BindView(R.id.inputLayoutLeadTracker)
    TextInputLayout inputLayoutLeadTracker;
    @BindView(R.id.inputLayoutOtherTracker)
    TextInputLayout inputLayoutOtherTracker;
    @BindView(R.id.addPhotoButton)
    Button addPhotoButton;

    private String mCurrentPhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trackers, container, false);
        //setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        //set the localized labels
        setLanguageValues();

        return view;
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
                        mCurrentPhotoPath = null;
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    final Uri selectedImageUri = data.getData();
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
    protected void setLanguageValues() {
        inputLayoutOrganisationName.setHint(localisedString("", R.string.organisation_name));
        inputLayoutLeadTracker.setHint(localisedString("", R.string.lead_tracker));
        inputLayoutOtherTracker.setHint(localisedString("", R.string.other_tracker));
        addPhotoButton.setText(localisedString("", R.string.group_selfie));
    }

    //validation
    @Override
    public String getValidationMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        if(TextUtils.isEmpty(editLeadTracker.getText())){
            stringBuilder.append(localisedString("", R.string.lead_tracker_missing_error));
            stringBuilder.append("\n");
        }

        if(TextUtils.isEmpty(editOrganisationName.getText())){
            stringBuilder.append(localisedString("", R.string.organisation_name_missing_error));
        }
        return stringBuilder.toString().trim();
    }
}
