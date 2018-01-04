package fragments.addtrack;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.Utils;
import au.csiro.ozatlas.model.ImageUploadResponse;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.addtrack.animal.AnimalFragment;
import fragments.addtrack.country.TrackCountryFragment;
import fragments.addtrack.map.TrackMapFragment;
import fragments.addtrack.trackers.TrackersFragment;
import fragments.setting.Language;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import model.map.Extent;
import model.map.Geometry;
import model.map.MapModel;
import model.map.MapResponse;
import model.map.Site;
import model.track.BilbyBlitzData;
import model.track.BilbyBlitzOutput;
import model.track.BilbyLocation;
import model.track.CheckMapInfo;
import model.track.ImageModel;
import model.track.TrackModel;
import retrofit2.Response;

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

    private boolean practiseView;

    private int imageUploadCount = 0;
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

        Bundle bundle = getArguments();
        if(bundle!=null) {
            practiseView = bundle.getBoolean(getString(R.string.practise_parameter));
        }

        pager.setOffscreenPageLimit(3);
        getDataForEdit();

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

        return view;
    }


    private void getDataForEdit() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            long primaryKey = bundle.getLong(getString(R.string.primary_key_parameter), -1);
            if(primaryKey!=-1) {
                RealmQuery<TrackModel> query = realm.where(TrackModel.class).equalTo("realmId", primaryKey);
                RealmResults<TrackModel> results = query.findAllAsync();
                results.addChangeListener(element -> {
                    trackModel = realm.copyFromRealm(element.first());
                    tabSetup();
                });
            }else{
                defaultSetup();
            }
            return;
        }

        defaultSetup();
    }

    private void defaultSetup(){
        trackModel.outputs = new RealmList<>();
        trackModel.activityId = getString(R.string.project_activity_id);
        BilbyBlitzOutput output = new BilbyBlitzOutput();
        output.selectFromSitesOnly = false;
        output.data = new BilbyBlitzData();
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
        inflater.inflate(R.menu.submit, menu);
        if(practiseView)
            menu.findItem(R.id.submit).setTitle("CLOSE");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when the user will press the submit menu item
            case R.id.submit:
                if(practiseView){
                    AtlasDialogManager.alertBox(getActivity(), getString(R.string.close_message), getString(R.string.close_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AtlasManager.hideKeyboard(getActivity());
                            setDrawerMenuChecked(R.id.home);
                            setDrawerMenuClicked(R.id.home);
                        }
                    });
                }else {
                    if (AtlasManager.isNetworkAvailable(getActivity())) {
                        String message;
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < NUMBER_OF_FRAGMENTS; i++) {
                            ValidationCheck validationCheck = (ValidationCheck) pagerAdapter.getRegisteredFragment(i);
                            if (validationCheck != null) {
                                message = validationCheck.getValidationMessage();
                                if (!TextUtils.isEmpty(message)) {
                                    stringBuilder.append("\n").append(message);
                                }
                            }
                        }
                        message = stringBuilder.toString();
                        if (!TextUtils.isEmpty(message))
                            showMultiLineSnackBarMessage(message);
                        else {
                            for (int j = 0; j < NUMBER_OF_FRAGMENTS; j++) {
                                BilbyDataManager bilbyDataManager = (BilbyDataManager) pagerAdapter.getRegisteredFragment(j);
                                if (bilbyDataManager != null) {
                                    bilbyDataManager.prepareData();
                                }
                            }
                            imageUploadCount = 0;
                            showProgressDialog();
                            MapModel mapModel = getMapModel(trackModel.outputs.get(0).data.tempLocations);
                            if (mapModel != null) {
                                uploadMap(mapModel);
                            } else {
                                uploadPhotos();
                            }
                        }
                    } else {
                        AtlasDialogManager.alertBox(getActivity(), getString(R.string.no_internet_message), getString(R.string.not_internet_title), (dialog, which) -> {
                            if (trackModel != null && !trackModel.isManaged()) {
                                for (int j = 0; j < NUMBER_OF_FRAGMENTS; j++) {
                                    BilbyDataManager bilbyDataManager = (BilbyDataManager) pagerAdapter.getRegisteredFragment(j);
                                    if (bilbyDataManager != null) {
                                        bilbyDataManager.prepareData();
                                    }
                                }
                                if (trackModel.realmId == null)
                                    trackModel.realmId = getPrimaryKeyValue();
                                realm.executeTransactionAsync(realm -> {
                                    realm.insertOrUpdate(trackModel);
                                    if (isAdded()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AtlasManager.hideKeyboard(getActivity());
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
                }
                break;
        }
        return true;
    }

    private long getPrimaryKeyValue() {
        Number number = realm.where(TrackModel.class).max("realmId");
        if (number == null)
            return 1L;
        return number.longValue() + 1;
    }

    @Override
    protected void setLanguageValues(Language language) {
        setTitle(localisedString("add_track", R.string.add_track));
    }

    private MapModel getMapModel(RealmList<BilbyLocation> tempLocations) {
        if (tempLocations != null && tempLocations.size() > 0) {
            MapModel mapModel = new MapModel();
            mapModel.pActivityId = getString(R.string.project_activity_id);
            mapModel.site = new Site();
            mapModel.site.name = "line 3";
            mapModel.site.projects = new String[]{getString(R.string.project_id)};
            mapModel.site.extent = new Extent();
            mapModel.site.extent.source = "drawn";
            mapModel.site.extent.geometry = new Geometry();
            mapModel.site.extent.geometry.areaKmSq = 0.0;
            mapModel.site.extent.geometry.type = "LineString";
            mapModel.site.extent.geometry.coordinates = new Double[tempLocations.size()][2];
            for (int i = 0; i < tempLocations.size(); i++) {
                BilbyLocation bilbyLocation = tempLocations.get(i);
                mapModel.site.extent.geometry.coordinates[i][0] = bilbyLocation.latitude;
                mapModel.site.extent.geometry.coordinates[i][1] = bilbyLocation.longitude;
            }
            return mapModel;
        }
        return null;
    }

    private void uploadMap(MapModel mapModel) {
        mCompositeDisposable.add(restClient.getService().postMap(mapModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<MapResponse>() {
                    @Override
                    public void onNext(MapResponse mapResponse) {
                        trackModel.siteId = mapResponse.id;
                        trackModel.outputs.get(0).checkMapInfo = new CheckMapInfo();
                        trackModel.outputs.get(0).checkMapInfo.validation = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        handleError(e, 0, "");
                    }

                    @Override
                    public void onComplete() {
                        uploadPhotos();
                    }
                }));
    }

    /**
     * upload photos
     */
    private void uploadPhotos() {
        if (trackModel.outputs.get(0).data.sightingEvidenceTable != null && imageUploadCount < trackModel.outputs.get(0).data.sightingEvidenceTable.size()) {
            mCompositeDisposable.add(restClient.getService().uploadPhoto(FileUtils.getMultipart(trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).mPhotoPath))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ImageUploadResponse>() {
                        @Override
                        public void onNext(ImageUploadResponse value) {
                            Log.d("", value.files[0].thumbnail_url);
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign = new RealmList<>();
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.add(new ImageModel());
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).thumbnailUrl = value.files[0].thumbnail_url;
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).url = value.files[0].url;
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).contentType = value.files[0].contentType;
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).staged = true;
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).dateTaken = value.files[0].date;
                            trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).filesize = value.files[0].size;
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            handleError(e, 0, "");
                        }

                        @Override
                        public void onComplete() {
                            imageUploadCount++;
                            if (imageUploadCount < trackModel.outputs.get(0).data.sightingEvidenceTable.size())
                                uploadPhotos();
                            else {
                                uploadLocationImages(trackModel);
                            }

                        }
                    }));
        } else {
            uploadLocationImages(trackModel);
        }
    }

    private void uploadLocationImages(final TrackModel trackModel) {
        if (trackModel.outputs.get(0).data.locationImage != null && trackModel.outputs.get(0).data.locationImage.size() > 0) {
            mCompositeDisposable.add(restClient.getService().uploadPhoto(FileUtils.getMultipart(trackModel.outputs.get(0).data.locationImage.get(0).mPhotoPath))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ImageUploadResponse>() {
                        @Override
                        public void onNext(ImageUploadResponse value) {
                            Log.d("", value.files[0].thumbnail_url);
                            trackModel.outputs.get(0).data.locationImage.get(0).thumbnailUrl = value.files[0].thumbnail_url;
                            trackModel.outputs.get(0).data.locationImage.get(0).url = value.files[0].url;
                            trackModel.outputs.get(0).data.locationImage.get(0).contentType = value.files[0].contentType;
                            trackModel.outputs.get(0).data.locationImage.get(0).staged = true;
                            trackModel.outputs.get(0).data.locationImage.get(0).dateTaken = value.files[0].date;
                            trackModel.outputs.get(0).data.locationImage.get(0).filesize = value.files[0].size;
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgressDialog();
                            handleError(e, 0, "");
                        }

                        @Override
                        public void onComplete() {
                                saveData();
                        }
                    }));
        } else {
            saveData();
        }
    }

    private void saveData() {
        mCompositeDisposable.add(restClient.getService().postTracks(getString(R.string.project_activity_id), trackModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(Response<Void> value) {
                        showSnackBarMessage("Sighting has been saved");
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        handleError(e, 0, "");
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();

                        if (trackModel.isManaged()) {
                            realm.beginTransaction();
                            trackModel.deleteFromRealm();
                            realm.commitTransaction();
                        }

                        if (getActivity() instanceof SingleFragmentActivity) {
                            getActivity().setResult(RESULT_OK);
                            getActivity().finish();
                        } else {
                            setDrawerMenuChecked(R.id.home);
                            setDrawerMenuClicked(R.id.home);
                        }
                    }
                }));
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
