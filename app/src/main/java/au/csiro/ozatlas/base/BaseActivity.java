package au.csiro.ozatlas.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param coordinatorLayout
     * @param string message to show
     */
    public void showSnackBarMessage(CoordinatorLayout coordinatorLayout, String string) {
        Snackbar.make(coordinatorLayout, string, Snackbar.LENGTH_LONG).show();
    }
}
