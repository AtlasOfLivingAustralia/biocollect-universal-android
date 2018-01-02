package fragments.addtrack.trackers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.addtrack.AddTrackFragment;
import fragments.addtrack.BilbyDataManager;
import fragments.addtrack.ValidationCheck;
import fragments.setting.Language;
import model.track.BilbyBlitzData;

/**
 * Created by sad038 on 19/9/17.
 */

public class TrackersFragment extends BaseMainActivityFragment implements ValidationCheck, BilbyDataManager {

    @BindView(R.id.editOrganisationName)
    EditText editOrganisationName;
    @BindView(R.id.editLeadTracker)
    EditText editLeadTracker;
    @BindView(R.id.editOtherTracker)
    EditText editOtherTracker;
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
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

        if (getParentFragment() instanceof AddTrackFragment) {
            bilbyBlitzData = ((AddTrackFragment) getParentFragment()).getBilbyBlitzData();
            setBilbyBlitzData();
        }

        return view;
    }

    public void setBilbyBlitzData() {
        editOrganisationName.setText(bilbyBlitzData.organisationName);
        if (bilbyBlitzData.recordedBy == null)
            editLeadTracker.setText(sharedPreferences.getUserDisplayName());
        else
            editLeadTracker.setText(bilbyBlitzData.recordedBy);
        editOtherTracker.setText(bilbyBlitzData.additionalTrackers);
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
    protected void setLanguageValues(Language language) {
        inputLayoutOrganisationName.setHint(localisedString("organisation_name", R.string.organisation_name));
        inputLayoutLeadTracker.setHint(localisedString("lead_tracker", R.string.lead_tracker));
        inputLayoutOtherTracker.setHint(localisedString("other_tracker", R.string.other_tracker));
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
            setError(inputLayoutLeadTracker, "");
        }

        if (TextUtils.isEmpty(editOrganisationName.getText())) {
            stringBuilder.append(localisedString("", R.string.organisation_name_missing_error));
            setError(inputLayoutOrganisationName, localisedString("", R.string.organisation_name_missing_error));
        } else {
            setError(inputLayoutOrganisationName, "");
        }
        return stringBuilder.toString().trim();
    }

    @Override
    public void prepareData() {
        bilbyBlitzData.recordedBy = editLeadTracker.getText().toString();
        bilbyBlitzData.organisationName = editOrganisationName.getText().toString();
        bilbyBlitzData.additionalTrackers = editOtherTracker.getText().toString();
    }
}
