package fragments.country;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 9/10/17.
 */

public class TrackCountryFragment extends BaseMainActivityFragment {
    @BindView(R.id.detailHabitatSpinner)
    AppCompatSpinner detailHabitatSpinner;
    @BindView(R.id.disturbanceSpinner)
    AppCompatSpinner disturbanceSpinner;
    @BindView(R.id.fireSpinner)
    AppCompatSpinner fireSpinner;
    @BindView(R.id.groundTypeSpinner)
    AppCompatSpinner groundTypeSpinner;
    @BindView(R.id.countryTypeSpinner)
    AppCompatSpinner countryTypeSpinner;
    @BindView(R.id.addPhotoButton)
    Button addPhotoButton;
    @BindView(R.id.editCountryName)
    EditText editCountryName;
    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_cuontry, container, false);
        //setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        return view;
    }
}
