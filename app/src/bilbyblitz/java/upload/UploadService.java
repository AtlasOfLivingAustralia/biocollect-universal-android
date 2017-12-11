package upload;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import application.CsiroApplication;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.rest.RestClient;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import model.map.Extent;
import model.map.Geometry;
import model.map.MapModel;
import model.map.MapResponse;
import model.map.Site;
import model.track.BilbyLocation;
import model.track.TrackModel;
import retrofit2.Response;

/**
 * Created by sad038 on 8/5/17.
 */

/**
 * An intent Service to upload the Draft Sightings
 * in Background
 */

public class UploadService extends IntentService {
    private final String TAG = "UploadService";
    @Inject
    protected RestClient restClient;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private BroadcastNotifier mBroadcaster;
    private Realm realm;
    private int imageUploadCount;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadService() {
        super("AtlasUploadService");
    }

    /**
     * when the service starts
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        CsiroApplication.component().inject(this);
        if (AtlasManager.isNetworkAvailable(this)) {
            realm = Realm.getDefaultInstance();
            //create the broadcaster to notify
            mBroadcaster = new BroadcastNotifier(this);

            ArrayList<Long> sightPrimarykeys = null;
            if (intent != null) {
                sightPrimarykeys = (ArrayList<Long>) intent.getSerializableExtra(getString(R.string.primary_keys_parameter));
            }

        /*
        get the primary keys of the models to upload
         */
            RealmResults<TrackModel> result;
            if (sightPrimarykeys == null) {
                result = realm.where(TrackModel.class).findAll();
            } else {
                RealmQuery<TrackModel> query = realm.where(TrackModel.class);
                query.equalTo("realmId", sightPrimarykeys.get(0));
                if (sightPrimarykeys.size() > 1) {
                    for (int i = 1; i < sightPrimarykeys.size(); i++) {
                        query.or().equalTo("realmId", sightPrimarykeys.get(i));
                    }
                }
                result = query.findAll();
            }

            //upload the sights
            Iterator<TrackModel> sightIterator = result.iterator();
            while (sightIterator.hasNext()) {
                TrackModel trackModel = sightIterator.next();
                //only those which are not being uploaded right now
                if (trackModel.isValid() && !trackModel.upLoading && getValidated(trackModel)) {
                    realm.beginTransaction();
                    trackModel.upLoading = true;
                    realm.commitTransaction();
                    mBroadcaster.notifyDataChange();
                    MapModel mapModel = getMapModel(trackModel.outputs.get(0).data.tempLocations);
                    if (mapModel != null) {
                        uploadMap(trackModel, mapModel);
                    } else {
                        uploadPhotos(trackModel);
                    }
                }
            }

            Log.d("", result.size() + "");
            if (realm != null)
                realm.close();
        }
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

    private void uploadMap(TrackModel trackModel, MapModel mapModel) {
        mCompositeDisposable.add(restClient.getService().postMap(mapModel, null)
                .subscribeWith(new DisposableObserver<MapResponse>() {
                    @Override
                    public void onNext(MapResponse mapResponse) {
                        trackModel.siteId = mapResponse.id;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        uploadPhotos(trackModel);
                    }
                }));
    }

    /**
     * data validation for uploading an Sight
     *
     * @return
     */
    private boolean getValidated(TrackModel trackModel) {
        return !TextUtils.isEmpty(trackModel.outputs.get(0).data.recordedBy) && !TextUtils.isEmpty(trackModel.outputs.get(0).data.organisationName) && !TextUtils.isEmpty(trackModel.outputs.get(0).data.surveyDate) && !TextUtils.isEmpty(trackModel.outputs.get(0).data.surveyStartTime) && trackModel.outputs.get(0).data.locationCentroidLatitude != null && trackModel.outputs.get(0).data.locationCentroidLongitude != null;
    }

    /**
     * upload photos first
     *
     * @param trackModel
     */
    private void uploadPhotos(final TrackModel trackModel) {
        if (trackModel.outputs.get(0).data.sightingEvidenceTable != null && imageUploadCount < trackModel.outputs.get(0).data.sightingEvidenceTable.size()) {
            mCompositeDisposable.add(restClient.getService().uploadPhoto(FileUtils.getMultipart(trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).mPhotoPath))
                    .subscribeWith(new DisposableObserver<ImageUploadResponse>() {
                        @Override
                        public void onNext(ImageUploadResponse value) {
                            Log.d("", value.files[0].thumbnail_url);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            imageUploadCount++;
                            if (imageUploadCount < trackModel.outputs.get(0).data.sightingEvidenceTable.size())
                                uploadPhotos(trackModel);
                            else {
                                saveData(trackModel);
                            }

                        }
                    }));
        } else {
            saveData(trackModel);
        }
    }


    /**
     * finally upload the sight
     *
     * @param trackModel
     */
    private void saveData(final TrackModel trackModel) {
        mCompositeDisposable.add(restClient.getService().postTracks(getString(R.string.project_activity_id), realm.copyFromRealm(trackModel))
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(Response<Void> value) {
                        Log.d("", "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        makeUploadingFalse(trackModel);
                        Log.d("", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        /*if (trackModel.outputs.get(0).data.sightingPhoto.size() > 0) {
                            Log.d(TAG, trackModel.outputs.get(0).data.sightingPhoto.get(0).thumbnailUrl);
                        }*/
                        realm.beginTransaction();
                        trackModel.deleteFromRealm();
                        realm.commitTransaction();

                        mBroadcaster.notifyDataChange();
                    }
                }));
    }

    /**
     * if something goes wrong then making the sight available to upload again.
     *
     * @param trackModel
     */
    private void makeUploadingFalse(final TrackModel trackModel) {
        realm.beginTransaction();
        trackModel.upLoading = false;
        realm.commitTransaction();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
