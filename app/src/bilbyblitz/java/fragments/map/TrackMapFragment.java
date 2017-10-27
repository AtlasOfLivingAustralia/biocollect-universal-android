package fragments.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.MapFragment;

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

    //private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_track_map, container, false);
        } catch (InflateException e) {
        *//* map is already there, just return view as it is *//*
        }*/

        View view = inflater.inflate(R.layout.fragment_track_map, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Fragment f = getChildFragmentManager().findFragmentById(R.id.mapFragment);
            if (f != null)
                getFragmentManager().beginTransaction().remove(f).commit();
        }catch (IllegalStateException e){

        }
    }
}
