package au.csiro.ozatlas.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseFragment extends Fragment {
    @Inject
    AtlasSharedPreferenceManager sharedPreferences;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OzAtlasApplication.component().inject(this);
    }
}
