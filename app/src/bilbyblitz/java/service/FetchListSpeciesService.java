package service;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseIntentService;
import au.csiro.ozatlas.model.KvpValues;
import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.rest.NetworkClient;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import rest.SpeciesListApiService;

import static model.EventBusPosts.FETCH_SPECIES_LIST;

public class FetchListSpeciesService extends BaseIntentService {

    /** The key name in the species list key value pair set that holds the Warumungu species name. (note spelling error) */
    private static final String WARUMUNGU_NAME_KEY = "Waramungu";
    private static final String WARLPIRI_NAME_KEY = "Warlpiri name";
    private static final String IMAGE_KEY = "Image";
    private static final String VERNACULAR_NAME_KEY = "vernacular name";

    private Realm realm;
    private SpeciesListApiService speciesListApiService;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchListSpeciesService() {
        super("FetchAndSaveSpeciesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        speciesListApiService = new NetworkClient(getString(R.string.species_list_url)).getRetrofit().create(SpeciesListApiService.class);

        realm = Realm.getDefaultInstance();
        fetchSpecies();
    }

    private void fetchSpecies() {
        mCompositeDisposable.add(speciesListApiService.getSpeciesList("dr8016")
                .subscribeWith(new DisposableObserver<List<SearchSpecies>>() {
                    @Override
                    public void onNext(List<SearchSpecies> value) {
                        realm.beginTransaction();
                        realm.delete(SearchSpecies.class);
                        realm.commitTransaction();
                        for (SearchSpecies searchSpecies : value) {
                            saveData(searchSpecies);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        EventBus.getDefault().post(FETCH_SPECIES_LIST);
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    private void saveData(final SearchSpecies species) {
        realm.executeTransactionAsync(realm -> {
            try {
                species.realmId = species.guid + species.id;
                Log.d(TAG + "ID", species.guid + "   " + species.id + "   " + species.realmId);
                if (species.kvpValues != null) {
                    for (KvpValues kvpValues : species.kvpValues) {
                        if (kvpValues.key.equals(WARLPIRI_NAME_KEY)) {
                            species.warlpiriName = kvpValues.value;
                        }

                        if (kvpValues.key.equals(VERNACULAR_NAME_KEY)) {
                            species.vernacularName = kvpValues.value;
                        }

                        if (kvpValues.key.equals(WARUMUNGU_NAME_KEY)) {
                            species.warumunguName = kvpValues.value;
                        }

                        if (kvpValues.key.equals(IMAGE_KEY)) {
                            species.imageUrl = kvpValues.value;
                        }
                    }
                }
                realm.copyToRealm(species);
            } catch (RealmPrimaryKeyConstraintException exception) {
                Log.d(TAG, getString(R.string.duplicate_entry));
            }
        });
    }
}
