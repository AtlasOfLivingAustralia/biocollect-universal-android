package fragments.trackers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 19/9/17.
 */

public class TrackersFragment extends BaseMainActivityFragment {
    @BindView(R.id.editOrganisationName)
    EditText editOrganisationName;
    @BindView(R.id.editLeadTracker)
    EditText editLeadTracker;
    @BindView(R.id.editOtherTracker)
    EditText editOtherTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trackers, container, false);
        setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        return view;
    }
}
