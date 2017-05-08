package au.csiro.ozatlas.upload;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.model.AddSight;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sad038 on 8/5/17.
 */

public class UploadService extends IntentService {

    //private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);
    final List<AddSight> sights = new ArrayList<>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadService() {
        super("AtlasUploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Long> sightPrimarykeys = null;
        if (intent != null) {
            sightPrimarykeys = (ArrayList<Long>) intent.getSerializableExtra(getString(R.string.primary_keys_parameter));
        }
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
        Log.d("", result.size() + "");
    }
}
