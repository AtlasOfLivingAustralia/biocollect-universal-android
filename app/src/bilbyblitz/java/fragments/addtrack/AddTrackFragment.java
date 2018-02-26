package fragments.addtrack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.Language;
import au.csiro.ozatlas.manager.Utils;
import au.csiro.ozatlas.model.Project;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.addtrack.animal.AnimalFragment;
import fragments.addtrack.country.TrackCountryFragment;
import fragments.addtrack.map.TrackMapFragment;
import fragments.addtrack.trackers.TrackersFragment;
import fragments.draft.DraftTrackListFragment;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import model.track.BilbyBlitzData;
import model.track.BilbyBlitzOutput;
import model.track.TrackModel;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 25/10/17.
 */

public class AddTrackFragment extends BaseMainActivityFragment {

    private final int NUMBER_OF_FRAGMENTS = 4;

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    public boolean acquireGPSLocation = true;
    private boolean practiseView;
    private TrackModel trackModel;

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
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        pager.setOffscreenPageLimit(3);
        Project project = sharedPreferences.getSelectedProject();
        if (project == null) {
            AtlasDialogManager.alertBox(getContext(), getString(R.string.project_selection_message), getString(R.string.project_selection_title), getString(R.string.setting), (dialogInterface, i) -> {
                setDrawerMenuChecked(R.id.nav_setting);
                setDrawerMenuClicked(R.id.nav_setting);
            }, true);
        }

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

        if (savedInstanceState != null) {
            acquireGPSLocation = savedInstanceState.getBoolean(getString(R.string.acquire_GPS_location_parameter));
            practiseView = savedInstanceState.getBoolean(getString(R.string.practise_parameter));
            trackModel = Parcels.unwrap(savedInstanceState.getParcelable(getString(R.string.track_model_parameter)));
            tabSetup();
        } else {
            Bundle bundle = getArguments();
            if (bundle != null) {
                practiseView = bundle.getBoolean(getString(R.string.practise_parameter));
            }
            getDataForEdit();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.acquire_GPS_location_parameter), acquireGPSLocation);
        outState.putBoolean(getString(R.string.practise_parameter), practiseView);
        outState.putParcelable(getString(R.string.track_model_parameter), Parcels.wrap(trackModel));
    }

    private void getDataForEdit() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            long primaryKey = bundle.getLong(getString(R.string.primary_key_parameter), -1);
            if (primaryKey != -1) {
                setTitle(getString(R.string.edit_track));
                RealmQuery<TrackModel> query = realm.where(TrackModel.class).equalTo("realmId", primaryKey);
                RealmResults<TrackModel> results = query.findAllAsync();
                results.addChangeListener(element -> {
                    if (isAdded()) {
                        trackModel = realm.copyFromRealm(element.first());
                        AtlasDialogManager.alertBox(getActivity(), getString(R.string.add_gps_location_in_edit), getString(R.string.gps_edit_title), "ADD", (dialog, id) -> {
                            dialog.dismiss();
                            acquireGPSLocation = true;
                            tabSetup();
                            results.removeAllChangeListeners();
                        }, "NO", (dialog, which) -> {
                            dialog.dismiss();
                            acquireGPSLocation = false;
                            tabSetup();
                        });
                    }
                });
            } else {
                defaultSetup();
            }
            return;
        }

        defaultSetup();
    }

    private void defaultSetup() {
        trackModel = new TrackModel();
        trackModel.outputs = new RealmList<>();
        BilbyBlitzOutput output = new BilbyBlitzOutput();
        output.selectFromSitesOnly = false;
        output.data = new BilbyBlitzData();
        output.name = getString(R.string.project_type);
        trackModel.outputs.add(output);
        tabSetup();
    }

    private void tabSetup() {
        pagerAdapter = new TrackerPagerAdapter();
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
        if (practiseView)
            menu.findItem(R.id.save).setTitle("FINISH");
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean isPractiseView() {
        return practiseView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when the user will press the submit menu item
            case R.id.save:
                if (practiseView) {
                    AtlasDialogManager.alertBox(getActivity(), getString(R.string.close_message), getString(R.string.close_title), (dialog, which) -> {
                        dialog.dismiss();
                        AtlasManager.hideKeyboard(getActivity());
                        setDrawerMenuChecked(R.id.home);
                        setDrawerMenuClicked(R.id.home);
                    });
                } else {
                    AtlasDialogManager.alertBox(getActivity(), getString(R.string.track_save_message),
                            getString(R.string.track_save_title),
                            getString(R.string.save), (dialog, which) -> {
                                dialog.dismiss();
                                saveLocally(true);
                            });
                }
                break;
        }
        return true;
    }

    public void saveLocally(boolean goToDraft) {
        if (trackModel != null && !trackModel.isManaged()) {
            for (int j = 0; j < NUMBER_OF_FRAGMENTS; j++) {
                BilbyDataManager bilbyDataManager = (BilbyDataManager) pagerAdapter.getRegisteredFragment(j);
                if (bilbyDataManager != null) {
                    bilbyDataManager.prepareData();
                }
            }
            if (trackModel.realmId == null) {
                trackModel.realmId = getPrimaryKeyValue();
            }

            realm.executeTransactionAsync(realm -> realm.insertOrUpdate(trackModel), () -> {
                if (goToDraft && isAdded()) {
                    AtlasManager.hideKeyboard(getActivity());
                    if (getActivity() instanceof SingleFragmentActivity) {
                        getActivity().setResult(RESULT_OK);
                        getActivity().finish();
                    } else {
                        showSnackBarMessage(getString(R.string.successful_local_save));
                        setDrawerMenuChecked(R.id.nav_review_track);
                        getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new DraftTrackListFragment()).commit();
                    }
                }
            }, error -> showSnackBarMessage(error.getMessage()));
        }
    }

    private long getPrimaryKeyValue() {
        Number number = realm.where(TrackModel.class).max("realmId");
        if (number == null)
            return 1L;
        return number.longValue() + 1;
    }

    @Override
    protected void setLanguageValues(Language language) {
        if (practiseView) {
            setTitle(localisedString("practise_track", R.string.practise_track));
        } else {
            setTitle(localisedString("add_track", R.string.add_track));
        }
    }

    private class TrackerPagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        TrackerPagerAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            //return registeredFragments.get(position);
            switch (position) {
                case 0:
                    return new TrackersFragment();
                case 1:
                    return new TrackMapFragment();
                case 2:
                    return new TrackCountryFragment();
                case 3:
                    return new AnimalFragment();
            }
            return null;
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

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment f = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    registeredFragments.put(0, f);
                    break;
                case 1:
                    registeredFragments.put(1, f);
                    break;
                case 2:
                    registeredFragments.put(2, f);
                    break;
                case 3:
                    registeredFragments.put(3, f);
                    break;
            }
            return f;
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
