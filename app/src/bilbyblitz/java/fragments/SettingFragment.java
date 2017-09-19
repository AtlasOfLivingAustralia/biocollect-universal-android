package fragments;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 19/9/17.
 */

public class SettingFragment extends BaseMainActivityFragment {
    @BindView(R.id.projectSpinner)
    Spinner projectSpinner;
    @BindView(R.id.languageSpinner)
    Spinner languageSpinner;
    @BindView(R.id.dataShareSwitch)
    SwitchCompat dataShareSwitch;
    @BindView(R.id.termsAgreeSwitch)
    SwitchCompat termsAgreeSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        return view;
    }
}
