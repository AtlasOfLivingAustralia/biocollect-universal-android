package fragments.trackers;

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
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragments.AddTrackFragment;
import fragments.ValidationCheck;
import model.track.BilbyBlitzData;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 19/9/17.
 */

public class TrackersFragment extends BaseMainActivityFragment implements ValidationCheck {

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

    private BilbyBlitzData bilbyBlitzData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trackers, container, false);
        //setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        //set the localized labels
        setLanguageValues();

        if(getParentFragment() instanceof AddTrackFragment){
            bilbyBlitzData = ((AddTrackFragment)getParentFragment()).getBilbyBlitzData();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void setLanguageValues() {
        inputLayoutOrganisationName.setHint(localisedString("", R.string.organisation_name));
        inputLayoutLeadTracker.setHint(localisedString("", R.string.lead_tracker));
        inputLayoutOtherTracker.setHint(localisedString("", R.string.other_tracker));
    }

    private void setError(TextInputLayout inputLayout, String error) {
        if (isAdded()) {
            inputLayout.setError(error);
        }
    }

    //validation
    @Override
    public String getValidationMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        if (TextUtils.isEmpty(editLeadTracker.getText())) {
            stringBuilder.append(localisedString("", R.string.lead_tracker_missing_error));
            stringBuilder.append("\n");
            setError(inputLayoutLeadTracker, localisedString("", R.string.lead_tracker_missing_error));
        } else {
            bilbyBlitzData.recordedBy = editLeadTracker.getText().toString();
            setError(inputLayoutLeadTracker, "");
        }

        if (TextUtils.isEmpty(editOrganisationName.getText())) {
            stringBuilder.append(localisedString("", R.string.organisation_name_missing_error));
            setError(inputLayoutOrganisationName, localisedString("", R.string.organisation_name_missing_error));
        } else {
            bilbyBlitzData.organisationName = editOrganisationName.getText().toString();
            setError(inputLayoutOrganisationName, "");
        }
        return stringBuilder.toString().trim();
    }
}
