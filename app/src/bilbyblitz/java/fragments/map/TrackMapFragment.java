package fragments.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 9/10/17.
 */

public class TrackMapFragment extends BaseMainActivityFragment {
    @BindView(R.id.surveySpinner)
    AppCompatSpinner surveySpinner;
    @BindView(R.id.startGPSButton)
    Button startGPSButton;
    @BindView(R.id.stopGPSButton)
    Button stopGPSButton;
    @BindView(R.id.editCentroidLatitude)
    EditText editCentroidLatitude;
    @BindView(R.id.editCentroidLongitude)
    EditText editCentroidLongitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_map, container, false);
        ButterKnife.bind(this, view);

        surveySpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.survey_type, R.layout.item_textview));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapLayout, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        //mapFragment.getMapAsync(callback);
    }

    @Override
    protected void setLanguageValues() {

    }
}
