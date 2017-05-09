package au.csiro.ozatlas.upload;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.rest.RestClient;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sad038 on 8/5/17.
 */

public class UploadService extends IntentService {

    @Inject
    protected RestClient restClient;

    private BroadcastNotifier mBroadcaster;
    private Realm realm;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadService() {
        super("AtlasUploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        OzAtlasApplication.component().inject(this);
        realm = Realm.getDefaultInstance();
        mBroadcaster = new BroadcastNotifier(this);

        ArrayList<Long> sightPrimarykeys = null;
        if (intent != null) {
            sightPrimarykeys = (ArrayList<Long>) intent.getSerializableExtra(getString(R.string.primary_keys_parameter));
        }

        /*
        get the primary keys of the models to upload
         */
        RealmResults<AddSight> result;
        if (sightPrimarykeys == null) {
            result = realm.where(AddSight.class).findAll();
        } else {
            RealmQuery<AddSight> query = realm.where(AddSight.class);
            query.equalTo("realmId", sightPrimarykeys.get(0));
            if (sightPrimarykeys.size() > 1) {
                for (int i = 1; i < sightPrimarykeys.size(); i++) {
                    query.or().equalTo("realmId", sightPrimarykeys.get(i));
                }
            }
            result = query.findAll();
        }

        Iterator<AddSight> sightIterator = result.iterator();
        while (sightIterator.hasNext()){
            AddSight addSight = sightIterator.next();
            realm.beginTransaction();
            addSight.upLoading = true;
            realm.commitTransaction();

            realm.beginTransaction();
            addSight.deleteFromRealm();
            realm.commitTransaction();

            mBroadcaster.notifyDataChange();
        }

        Log.d("", result.size() + "");
        if (realm != null)
            realm.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
