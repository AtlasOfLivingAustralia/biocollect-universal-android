package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;

/**
 * Created by sad038 on 8/6/17.
 */

public class ContactUsFragment extends BaseMainActivityFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        setTitle(getString(R.string.contact_us_title));
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        sendAnalyticsScreenName("Contact Us", TAG);
    }
}