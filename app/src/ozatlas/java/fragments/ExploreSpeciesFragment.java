package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 21/6/17.
 */

public class ExploreSpeciesFragment extends BaseMainActivityFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_species, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.explore_species_title));
        return view;
    }
}
