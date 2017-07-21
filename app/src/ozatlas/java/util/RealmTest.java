package util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

import au.csiro.ozatlas.model.Species;
import au.csiro.ozatlas.model.SpeciesSearchResponse;
import io.realm.Realm;

/**
 * Created by sad038 on 20/7/17.
 */

public class RealmTest {
    final String TAG = "RealmTest";
    Realm realm = Realm.getDefaultInstance();
    File file;

    public RealmTest() {
        file = new File(realm.getPath());
    }

    //10000 = 1MB // 9MB
    //100000 = 8.5 MB //75MB
    //1000000 = 83 MB //218MB

    public void insert() {
        Log.d(TAG, "DB Size:"+ getDBSize());
        realm.beginTransaction();
        for(int i=0;i<1000000;i++){
            realm.copyToRealm(getDemoSpecies());
        }
        realm.commitTransaction();
        Log.d(TAG, "Rows: " + realm.where(Species.class).count());
        Log.d(TAG, "DB Size:"+ getDBSize());
        Realm.compactRealm(realm.getConfiguration());
        Log.d(TAG, "DB Size:"+ getDBSize());
    }

    private Species getDemoSpecies(){
        Species species = new Species();
        species.guid = "123123123123";
        species.name = "demo";
        species.commonName="demo kingdom";
        species.outputSpeciesId = "highlight";
        return species;
    }

    public long freeMemory()
    {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   free   = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        return free;
    }

    public long getDBSize() {
        return file.length();
    }
}
