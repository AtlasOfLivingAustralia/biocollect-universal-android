package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;
import base.BaseMainActivityFragment;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 30/6/17.
 */

public class SpeciesDetailFragment extends BaseMainActivityFragment {
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
        return view;
    }
}
