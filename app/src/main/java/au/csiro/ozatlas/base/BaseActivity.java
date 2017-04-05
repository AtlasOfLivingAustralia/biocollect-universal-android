package au.csiro.ozatlas.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseActivity extends AppCompatActivity {
    @Inject
    AtlasSharedPreferenceManager sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OzAtlasApplication.component().inject(this);
    }
}
