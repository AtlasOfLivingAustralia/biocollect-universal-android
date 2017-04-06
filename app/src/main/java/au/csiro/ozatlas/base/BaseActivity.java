package au.csiro.ozatlas.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import au.csiro.ozatlas.rest.RestClient;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseActivity extends AppCompatActivity {
    @Inject
    protected AtlasSharedPreferenceManager sharedPreferences;

    @Inject
    protected RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OzAtlasApplication.component().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AtlasManager.eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AtlasManager.eventBus.unregister(this);
    }
}
