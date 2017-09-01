package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;
import base.BaseMainActivityFragment;

/**
 * Created by sad038 on 1/9/17.
 */

public class HomePageFragment extends BaseMainActivityFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        setTitle(getString(R.string.app_name));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Home Page", TAG);
    }

    private class ListItem{
        String test;
        int icon;
    }


}
