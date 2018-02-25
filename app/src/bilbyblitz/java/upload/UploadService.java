package upload;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseIntentService;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.Utils;
import au.csiro.ozatlas.model.ImageUploadResponse;
import au.csiro.ozatlas.model.Project;
import au.csiro.ozatlas.model.map.CheckMapInfo;
import au.csiro.ozatlas.model.map.MapResponse;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import model.map.Extent;
import model.map.Geometry;
import model.map.MapModel;
import model.map.Site;
import model.track.BilbyLocation;
import model.track.ImageModel;
import model.track.TrackModel;
import retrofit2.Response;

import static upload.UploadService.UploadNotification.INTERRUPTED;

/**
 * Created by sad038 on 8/5/17.
 */

/**
 * An intent Service to upload the Draft Sightings
 * in Background
 */

public class UploadService extends BaseIntentService {
    private final String TAG = "UploadService";
    private final int SUCCESS_NOTIFICATION_ID = 1;
    private final int ERROR_NOTIFICATION_ID = 0;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Realm realm;
    private int imageUploadCount;
    private int trackCount = 0;

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
        //project = sharedPreferences.getSelectedProject();

        if (AtlasManager.isNetworkAvailable(this)) {
            Project project = sharedPreferences.getSelectedProject();
            if (project != null) {
                realm = Realm.getDefaultInstance();

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
                    imageUploadCount = 0;
                    trackCount++;
                    TrackModel trackModel = sightIterator.next();
                    //only those which are not being uploaded right now
                    if (trackModel.isValid() && !trackModel.upLoading && getValidated(realm.copyFromRealm(trackModel))) {
                        realm.beginTransaction();
                        trackModel.upLoading = true;
                        trackModel.projectName = project.name;
                        trackModel.projectId = project.projectId;
                        trackModel.type = getString(R.string.project_type);
                        trackModel.activityId = project.projectActivityId;
                        trackModel.outputs.get(0).data.organisationName = project.name;
                        realm.commitTransaction();

                        EventBus.getDefault().post(new UploadNotificationModel(UploadNotification.UPLOAD_STARTED, trackCount));
                        MapModel mapModel = getMapModel(trackModel.projectName, trackModel.activityId, trackModel.projectId, trackModel.outputs.get(0).data.tempLocations);
                        if (mapModel != null) {
                            uploadMap(trackModel, mapModel);
                        } else {
                            postNotification(ERROR_NOTIFICATION_ID, "Please complete the Track information.");
                        }
                    } else {
                        postNotification(ERROR_NOTIFICATION_ID, "Please complete the Track information.");
                    }
                }

                Log.d("", result.size() + "");
                if (realm != null)
                    realm.close();
            } else {
                postNotification(ERROR_NOTIFICATION_ID, getString(R.string.project_selection_message));
            }
        }
    }

    private MapModel getMapModel(String projectName, String projectActivityId, String projectId, RealmList<BilbyLocation> tempLocations) {
        if (tempLocations != null && tempLocations.size() > 0) {
            MapModel mapModel = new MapModel();
            mapModel.site = new Site();

            mapModel.pActivityId = projectActivityId;
            mapModel.site.name = projectName + "-" + UUID.randomUUID().toString();

            mapModel.site.visibility = "private";
            mapModel.site.asyncUpdate = true;
            mapModel.site.projects = new String[]{projectId};
            mapModel.site.extent = new Extent();
            mapModel.site.extent.source = "drawn";
            mapModel.site.extent.geometry = new Geometry();
            mapModel.site.extent.geometry.areaKmSq = 0.0;
            mapModel.site.extent.geometry.type = "LineString";
            if (tempLocations.size() > 0) {
                mapModel.site.extent.geometry.centre = new Double[2];
                mapModel.site.extent.geometry.centre[0] = tempLocations.get(0).longitude;
                mapModel.site.extent.geometry.centre[1] = tempLocations.get(0).latitude;
            }
            mapModel.site.extent.geometry.coordinates = new Double[tempLocations.size()][2];
            //mapModel.site.extent.geometry.coordinates = new Double[3000][2];
            for (int i = 0; i < tempLocations.size(); i++) {
                BilbyLocation bilbyLocation = tempLocations.get(i);
                mapModel.site.extent.geometry.coordinates[i][0] = bilbyLocation.longitude;
                mapModel.site.extent.geometry.coordinates[i][1] = bilbyLocation.latitude;
            }

            /*for (int i = 1; i < 3000; i++) {
                mapModel.site.extent.geometry.coordinates[i][0] = mapModel.site.extent.geometry.coordinates[i-1][0] + .01;
                mapModel.site.extent.geometry.coordinates[i][1] = mapModel.site.extent.geometry.coordinates[i-1][1] + .01;
            }*/

            return mapModel;
        }
        return null;
    }

    private void uploadMap(TrackModel trackModel, MapModel mapModel) {
        mCompositeDisposable.add(restClient.getService().postMap(mapModel)
                .subscribeWith(new DisposableObserver<MapResponse>() {
                    @Override
                    public void onNext(MapResponse mapResponse) {
                        realm.beginTransaction();
                        trackModel.siteId = mapResponse.id;
                        trackModel.outputs.get(0).checkMapInfo = realm.createObject(CheckMapInfo.class);
                        trackModel.outputs.get(0).checkMapInfo.validation = true;
                        trackModel.outputs.get(0).data.location = mapResponse.id;
                        realm.commitTransaction();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                        makeUploadingFalse(trackModel, getString(R.string.map_upload_fail));
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
        return !TextUtils.isEmpty(trackModel.outputs.get(0).data.recordedBy) &&
                !TextUtils.isEmpty(trackModel.outputs.get(0).data.organisationName) &&
                !TextUtils.isEmpty(trackModel.outputs.get(0).data.surveyDate) &&
                !TextUtils.isEmpty(trackModel.outputs.get(0).data.surveyStartTime) &&
                trackModel.outputs.get(0).data.locationLatitude != null &&
                trackModel.outputs.get(0).data.locationLongitude != null &&
                trackModel.outputs.get(0).data.tempLocations.size() > 1 &&
                trackModel.outputs.get(0).data.sightingEvidenceTable != null &&
                trackModel.outputs.get(0).data.sightingEvidenceTable.size() > 0;
    }

    /**
     * upload photos first
     *
     * @param trackModel
     */
    private void uploadPhotos(final TrackModel trackModel) {
        if (trackModel.outputs.get(0).data.sightingEvidenceTable != null && imageUploadCount < trackModel.outputs.get(0).data.sightingEvidenceTable.size()) {
            if (trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).mPhotoPath != null) {
                mCompositeDisposable.add(restClient.getService().uploadPhoto(FileUtils.getMultipart(trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).mPhotoPath))
                        .subscribeWith(new DisposableObserver<ImageUploadResponse>() {
                            @Override
                            public void onNext(ImageUploadResponse value) {
                                Log.d("", value.files[0].thumbnail_url);
                                realm.beginTransaction();
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign = new RealmList<>();
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.add(realm.createObject(ImageModel.class));
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).thumbnailUrl = value.files[0].thumbnail_url;
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).url = value.files[0].url;
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).contentType = value.files[0].contentType;
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).staged = true;
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).dateTaken = value.files[0].date;
                                trackModel.outputs.get(0).data.sightingEvidenceTable.get(imageUploadCount).imageOfSign.get(0).filesize = value.files[0].size;
                                realm.commitTransaction();
                            }

                            @Override
                            public void onError(Throwable e) {
                                makeUploadingFalse(trackModel, getString(R.string.species_photo_upload_fail));
                            }

                            @Override
                            public void onComplete() {
                                imageUploadCount++;
                                if (imageUploadCount < trackModel.outputs.get(0).data.sightingEvidenceTable.size())
                                    uploadPhotos(trackModel);
                                else {
                                    uploadLocationImages(trackModel);
                                }

                            }
                        }));
            } else {
                imageUploadCount++;
                if (imageUploadCount < trackModel.outputs.get(0).data.sightingEvidenceTable.size())
                    uploadPhotos(trackModel);
                else {
                    uploadLocationImages(trackModel);
                }
            }
        } else {
            uploadLocationImages(trackModel);
        }
    }

    private void uploadLocationImages(final TrackModel trackModel) {
        if (trackModel.outputs.get(0).data.locationImage != null && trackModel.outputs.get(0).data.locationImage.size() > 0) {
            mCompositeDisposable.add(restClient.getService().uploadPhoto(FileUtils.getMultipart(trackModel.outputs.get(0).data.locationImage.get(0).mPhotoPath))
                    .subscribeWith(new DisposableObserver<ImageUploadResponse>() {
                        @Override
                        public void onNext(ImageUploadResponse value) {
                            Log.d("", value.files[0].thumbnail_url);
                            realm.beginTransaction();
                            trackModel.outputs.get(0).data.locationImage.get(0).thumbnailUrl = value.files[0].thumbnail_url;
                            trackModel.outputs.get(0).data.locationImage.get(0).url = value.files[0].url;
                            trackModel.outputs.get(0).data.locationImage.get(0).contentType = value.files[0].contentType;
                            trackModel.outputs.get(0).data.locationImage.get(0).staged = true;
                            trackModel.outputs.get(0).data.locationImage.get(0).dateTaken = value.files[0].date;
                            trackModel.outputs.get(0).data.locationImage.get(0).filesize = value.files[0].size;
                            realm.commitTransaction();
                        }

                        @Override
                        public void onError(Throwable e) {
                            makeUploadingFalse(trackModel, getString(R.string.country_photo_upload_fail));
                        }

                        @Override
                        public void onComplete() {
                            saveData(trackModel);
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
        Log.d(TAG, new Gson().toJson(realm.copyFromRealm(trackModel)));
        mCompositeDisposable.add(restClient.getService().postTracks(trackModel.activityId, realm.copyFromRealm(trackModel))
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(Response<Void> value) {
                        Log.d("UPLOAD BIlBY", "onNext" + value.code() + value.message());
                        if (value.isSuccessful()) {
                            postNotification(SUCCESS_NOTIFICATION_ID, Utils.ordinal(trackCount) + " track has been successfully uploaded");
                            realm.beginTransaction();
                            trackModel.deleteFromRealm();
                            realm.commitTransaction();
                            EventBus.getDefault().post(new UploadNotificationModel(UploadNotification.UPLOAD_DONE, trackCount));
                        } else {
                            makeUploadingFalse(trackModel, getString(R.string.authentication_error));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        makeUploadingFalse(trackModel, getString(R.string.track_upload_fail));
                        Log.d("", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    /**
     * if something goes wrong then making the sight available to upload again.
     *
     * @param trackModel
     */
    private void makeUploadingFalse(final TrackModel trackModel, String message) {
        realm.beginTransaction();
        trackModel.upLoading = false;
        realm.commitTransaction();
        EventBus.getDefault().post(new UploadNotificationModel(INTERRUPTED, trackCount));
        postNotification(ERROR_NOTIFICATION_ID, message);
    }

    private void postNotification(int mNotificationId, String message) {
        // The id of the channel.
        String CHANNEL_ID = "bilby_channel";
        String CHANNEL_NAME = "Tracks App";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_bilby_blitz)
                        .setAutoCancel(true).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mBuilder.setPriority(Notification.PRIORITY_MAX);

        switch (mNotificationId) {
            case SUCCESS_NOTIFICATION_ID:
                mBuilder.setContentTitle("Upload Successful");
                break;
            case ERROR_NOTIFICATION_ID:
                mBuilder.setContentTitle("Upload Unsuccessful");
                break;
        }
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        if (mNotificationManager != null)
            mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    public enum UploadNotification {
        UPLOAD_STARTED,
        UPLOAD_DONE,
        UPLOAD_ALL_DONE,
        INTERRUPTED,
    }

    public class UploadNotificationModel {
        public UploadNotification uploadNotification;
        public int index;

        public UploadNotificationModel(UploadNotification uploadNotification, int index) {
            this.uploadNotification = uploadNotification;
            this.index = index;
        }
    }
}
