package fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;
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

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    final int NUMBER_OF_FRAGMENTS = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_track, container, false);
        setTitle(getString(R.string.add_track));
        ButterKnife.bind(this, view);
        pager.setAdapter(new PagerAdapter());
        tabLayout.setupWithViewPager(pager);
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
        return view;
    }

    private TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(tab.getPosition()==3){
                showFloatingButton();
            }else{
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
    protected void setLanguageValues() {

    }

    private class PagerAdapter extends FragmentPagerAdapter {
        PagerAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TrackersFragment();
                case 1:
                    return new TrackMapFragment();
                case 2:
                    return new TrackCountryFragment();
                case 3:
                    return new AnimalFragment();
                default:
                    return null;
            }
        }


        @Override
        public CharSequence getPageTitle(int position){
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

        @Override
        public int getCount() {
            return NUMBER_OF_FRAGMENTS;
        }
    }
}
