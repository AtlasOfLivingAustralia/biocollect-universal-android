package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 21/6/17.
 */

public class ExploreSpeciesFragment extends BaseMainActivityFragment implements OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_species, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.explore_species_title));

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
