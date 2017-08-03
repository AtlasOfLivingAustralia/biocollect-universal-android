package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.fragments.WebViewFragment;
import base.BaseMainActivityFragment;
import butterknife.ButterKnife;
import model.ExploreAnimal;

/**
 * Created by sad038 on 30/6/17.
 */

/**
 * This class is to show the detail of a selected species.
 */
public class SpeciesDetailFragment extends BaseMainActivityFragment {
    /**
     * click listener for "record a sight" button
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = getArguments();
            Fragment fragment = new AddSightingFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_species_detail, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        view.findViewById(R.id.record).setOnClickListener(onClickListener);

        //Retrieving the selected species
        ExploreAnimal animal = (ExploreAnimal) getArguments().getSerializable(getString(R.string.species_parameter));
        if (animal != null) {
            Bundle bundle = new Bundle();
            //showing the detail of the species in a fragment.
            bundle.putString(getString(R.string.url_parameter), String.format(Locale.getDefault(), "http://bie.ala.org.au/species/%s", animal.guid));
            Fragment fragment = new WebViewFragment();
            fragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction().replace(R.id.speciesFragmentHolder, fragment).commit();
        }
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        sendAnalyticsScreenName("Species Detail");
    }
}
