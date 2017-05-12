package au.csiro.ozatlas.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.base.MainActivityFragmentListener;
import au.csiro.ozatlas.fragments.AddSightingFragment;
import au.csiro.ozatlas.fragments.DraftSightingListFragment;
import au.csiro.ozatlas.fragments.SightingListFragment;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.upload.Constants;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MainActivityFragmentListener {

    private NavigationView navigationView;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private DataChangeNotificationReceiver dataChangeNotificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        // Instantiates a new DownloadStateReceiver
        dataChangeNotificationReceiver = new DataChangeNotificationReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(dataChangeNotificationReceiver, statusIntentFilter);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new AddSightingFragment()).commit();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtlasManager.hideKeyboard(MainActivity.this);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavigationHeader();

        if (AtlasManager.isTesting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new DraftSightingListFragment()).commit();
        } else {
            navigationView.getMenu().findItem(R.id.nav_all_sighting).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SightingListFragment()).commit();
        }
    }

    private void updateNavigationHeader() {
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.name)).setText(sharedPreferences.getUserDisplayName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email)).setText(sharedPreferences.getUsername());
        //((CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView)).setImageURL(user.getPhotoUrl().toString());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            sharedPreferences.writeAuthKey("");
            launchLoginActivity();
        } else if (id == R.id.nav_add) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new AddSightingFragment()).commit();
        } else if (id == R.id.nav_all_sighting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SightingListFragment()).commit();
        } else if (id == R.id.nav_my_sighting) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.myview_parameter), "myrecords");
            Fragment fragment = new SightingListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
        } else if (id == R.id.nav_about) {
            startWebViewActivity(getString(R.string.about_us_url), getString(R.string.about_title));
        } else if (id == R.id.nav_contact) {
            startWebViewActivity(getString(R.string.contact_us_url), getString(R.string.contact_us_title));
        }else if(id == R.id.nav_draft_sighting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new DraftSightingListFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataChangeNotificationReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(dataChangeNotificationReceiver);
    }

    @Override
    public void hideFloatingButton() {
        if (fab.getScaleX() != 0.0f)
            fab.animate().scaleX(0.0f).scaleY(0.0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void showFloatingButton() {
        if (fab.getScaleX() != 1.0f)
            fab.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void showSnackBarMessage(String string) {
        showSnackBarMessage(coordinatorLayout, string);
    }

    @Override
    public void handleError(Throwable e, int code, String message) {
        handleError(coordinatorLayout, e, code, message);
    }

    private class DataChangeNotificationReceiver extends BroadcastReceiver {
        //prevent instantiation
        private DataChangeNotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentHolder);
            if(fragment!=null && fragment instanceof DraftSightingListFragment){
                ((DraftSightingListFragment)fragment).readDraftSights();
            }
        }
    }
}
