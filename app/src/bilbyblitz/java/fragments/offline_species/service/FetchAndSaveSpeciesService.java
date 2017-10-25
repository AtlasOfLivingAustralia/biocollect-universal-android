package fragments.offline_species.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseIntentService;
import au.csiro.ozatlas.model.ExploreGroup;
import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.rest.BioCacheApiService;
import au.csiro.ozatlas.rest.NetworkClient;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class FetchAndSaveSpeciesService extends BaseIntentService {
    protected final static int MAX = 20;
    private final int NOTIFICATION_ID = 1;
    private final String FQ = "geospatial_kosher:true";
    private final String FACET = "species_group";
    private ExploreGroup group;
    private double latitude, longitude, radius;
    private int count = 0;

    private BioCacheApiService bioCacheApiService;
    private Realm realm;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchAndSaveSpeciesService() {
        super("FetchAndSaveSpeciesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            group = (ExploreGroup) intent.getSerializableExtra(getString(R.string.group_parameter));
            latitude = intent.getDoubleExtra(getString(R.string.latitude_parameter), 0);
            longitude = intent.getDoubleExtra(getString(R.string.longitude_parameter), 0);
            radius = intent.getDoubleExtra(getString(R.string.radius_parameter), 0);
        }
        if (group != null && latitude != 0 && longitude != 0 && radius != 0) {
            bioCacheApiService = new NetworkClient(getString(R.string.bio_cache_url)).getRetrofit().create(BioCacheApiService.class);
            realm = Realm.getDefaultInstance();
            makeNotification(false, getString(R.string.download_service_started), android.R.drawable.stat_sys_download);
            fetchAnimals(group.name, 0);
        }
    }

    private void fetchAnimals(final String groupName, final int offset) {
        mCompositeDisposable.add(bioCacheApiService.getSpeciesFromMap(groupName, FQ, FACET, latitude, longitude, radius, MAX, offset) //27.76, 138.55, 532.0)//
                .subscribeWith(new DisposableObserver<List<SearchSpecies>>() {
                    @Override
                    public void onNext(List<SearchSpecies> value) {
                        if (value != null) {
                            count = count + value.size();
                            for (SearchSpecies searchSpecies : value) {
                                saveData(searchSpecies);
                            }

                            if (group.speciesCount > count) {
                                fetchAnimals(group.name, offset + MAX);
                            } else {
                                makeNotification(true, getString(R.string.download_service_complete), R.drawable.ic_stat_bilby_blitz);
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        makeNotification(true, getString(R.string.download_interrupted), android.R.drawable.stat_notify_error);
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    private void makeNotification(boolean isSound, String msg, int res) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(res)
                        .setContentTitle(getString(R.string.app_name))
                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                        .setContentText(msg)
                        .setAutoCancel(true);

        if (isSound) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(defaultSoundUri);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void saveData(final SearchSpecies species) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    species.realmId = species.guid + species.id;
                    Log.d(TAG + "ID", species.guid + "   " + species.id + "   " + species.realmId);
                    realm.copyToRealm(species);
                } catch (RealmPrimaryKeyConstraintException exception) {
                    Log.d(TAG, getString(R.string.duplicate_entry));
                }
            }
        });
    }
}
