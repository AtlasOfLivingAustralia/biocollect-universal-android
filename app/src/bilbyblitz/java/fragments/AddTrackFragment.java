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
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.Utils;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.animal.AnimalFragment;
import fragments.country.TrackCountryFragment;
import fragments.map.TrackMapFragment;
import fragments.trackers.TrackersFragment;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import model.track.BilbyBlitzData;
import model.track.BilbyBlitzOutput;
import model.track.TrackModel;

/**
 * Created by sad038 on 25/10/17.
 */

public class AddTrackFragment extends BaseMainActivityFragment {

    private final int NUMBER_OF_FRAGMENTS = 4;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private TrackModel trackModel = new TrackModel();
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

    public BilbyBlitzData getBilbyBlitzData() {
        return trackModel.outputs.get(0).data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_track, container, false);
        setTitle(getString(R.string.add_track));
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        getDataForEdit();

        //set the localized labels
        setLanguageValues();

        return view;
    }


    private void getDataForEdit() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int primaryKey = bundle.getInt(getString(R.string.primary_key_parameter));
            RealmQuery<TrackModel> query = realm.where(TrackModel.class).equalTo("realmId", primaryKey);
            RealmResults<TrackModel> results = query.findAllAsync();
            results.addChangeListener(element -> {
                trackModel = element.first();
                tabSetup();
            });
            return;
        }

        trackModel.outputs = new RealmList<>();
        BilbyBlitzOutput output = new BilbyBlitzOutput();
        output.data = new BilbyBlitzData();
        trackModel.outputs.add(output);
        tabSetup();
    }

    private void tabSetup(){
        pagerAdapter = new TrackerPagerAdapter();
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
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
                if (AtlasManager.isNetworkAvailable(getActivity())) {
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
                    else {

                    }
                } else {
                    AtlasDialogManager.alertBox(getActivity(), getString(R.string.no_internet_message), getString(R.string.not_internet_title), (dialog, which) -> {
                        if (trackModel != null && !trackModel.isManaged()) {
                            trackModel.realmId = getPrimaryKeyValue();
                            realm.executeTransactionAsync(realm -> {
                                realm.copyToRealm(trackModel);
                                if (isAdded()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showSnackBarMessage("Your track information has been saved as Draft");
                                            setDrawerMenuChecked(R.id.nav_review_track);
                                            setDrawerMenuClicked(R.id.nav_review_track);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                break;
        }
        return true;
    }

    private long getPrimaryKeyValue() {
        Number number = realm.where(TrackModel.class).max("realmId");
        if(number==null)
            return 1L;
        return number.longValue() + 1;
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
