package fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.Utils;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.animal.AnimalFragment;
import fragments.country.TrackCountryFragment;
import fragments.map.TrackMapFragment;
import fragments.trackers.TrackersFragment;

/**
 * Created by sad038 on 25/10/17.
 */

public class AddTrackFragment extends BaseMainActivityFragment {

    private final int NUMBER_OF_FRAGMENTS = 4;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private TrackerPagerAdapter pagerAdapter;
    private TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Utils.closeKeyboard(getActivity(), getView().getWindowToken());
            if (tab.getPosition() == 3) {
                showFloatingButton();
            } else {
                hideFloatingButton();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_track, container, false);
        setTitle(getString(R.string.add_track));
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        pagerAdapter = new TrackerPagerAdapter();
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        tabLayout.addOnTabSelectedListener(tabSelectedListener);

        //set the localized labels
        setLanguageValues();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when the user will press the submit menu item
            case R.id.submit:
                String message;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < NUMBER_OF_FRAGMENTS; i++) {
                    ValidationCheck validationCheck = (ValidationCheck) pagerAdapter.getRegisteredFragment(i);
                    if (validationCheck != null) {
                        message = validationCheck.getValidationMessage();
                        if (!TextUtils.isEmpty(message))
                            stringBuilder.append("\n").append(message);
                    }
                }
                message = stringBuilder.toString();
                if (!TextUtils.isEmpty(message))
                    showMultiLineSnackBarMessage(message);
                break;
        }
        return true;
    }

    @Override
    protected void setLanguageValues() {
        setTitle(localisedString("add_track", R.string.add_track));
    }

    private class TrackerPagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        TrackerPagerAdapter() {
            super(getChildFragmentManager());
            registeredFragments.put(0, new TrackersFragment());
            registeredFragments.put(1, new TrackMapFragment());
            registeredFragments.put(2, new TrackCountryFragment());
            registeredFragments.put(3, new AnimalFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tracker);
                case 1:
                    return getString(R.string.map);
                case 2:
                    return getString(R.string.country);
                case 3:
                    return getString(R.string.animals);
                default:
                    return null;
            }
        }

        Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            return NUMBER_OF_FRAGMENTS;
        }
    }
}
