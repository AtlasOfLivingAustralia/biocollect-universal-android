package activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import au.csiro.ozatlas.BuildConfig;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.MainActivityFragmentListener;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import fragments.addtrack.AddTrackFragment;
import fragments.home.HomePageFragment;
import fragments.draft.DraftTrackListFragment;
import au.csiro.ozatlas.manager.Language;
import fragments.setting.SettingFragment;

/**
 * This activity holds most of the basic fragments or functionality that a user can do
 * Basically shows the navigation drawer nd all its fragments
 */
public class MainActivity extends BilbyBlitzBaseActivity implements NavigationView.OnNavigationItemSelectedListener, MainActivityFragmentListener {

    private NavigationView navigationView;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void setLanguageValues(Language language) {
        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        // find MenuItem to change
        menu.findItem(R.id.home).setTitle(localisedString("home", R.string.home));
        menu.findItem(R.id.nav_add_track).setTitle(localisedString("add_track", R.string.add_track));
        menu.findItem(R.id.nav_practise_track).setTitle(localisedString("practise_track", R.string.practise_track));
        menu.findItem(R.id.nav_review_track).setTitle(localisedString("review_track", R.string.review_track));
        menu.findItem(R.id.nav_setting).setTitle(localisedString("setting", R.string.setting));
        menu.findItem(R.id.nav_logout).setTitle(localisedString("logout", R.string.logout));
        menu.findItem(R.id.nav_help).setTitle(localisedString("help", R.string.help));
        menu.findItem(R.id.nav_contact).setTitle(localisedString("about", R.string.about));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Navigation Drawer setup
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

        if (BuildConfig.DEBUG) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomePageFragment()).commit();
        } else {
            navigationView.getMenu().findItem(R.id.home).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomePageFragment()).commit();
        }

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());
    }

    /**
     * navigation bar header information
     */
    private void updateNavigationHeader() {
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.name)).setText(sharedPreferences.getUserDisplayName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email)).setText(sharedPreferences.getUsername());
    }

    /**
     * when the user presse back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * navigation drawer items click listener
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomePageFragment()).commit();
        } else if (id == R.id.nav_logout) {
            AtlasDialogManager.alertBox(this, getString(R.string.logout_message), getString(R.string.logout_title), getString(R.string.logout_title), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    launchLoginActivity();
                }
            });
        } else if (id == R.id.nav_setting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SettingFragment()).commit();
        } else if (id == R.id.nav_add_track) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new AddTrackFragment()).commit();
        } else if (id == R.id.nav_practise_track) {
            AddTrackFragment fragment = new AddTrackFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.practise_parameter), true);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
        }else if (id == R.id.nav_review_track) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new DraftTrackListFragment()).commit();
        }/*else if (id == R.id.nav_my_projects) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.user_project_parameter), true);
            Fragment fragment = new ProjectListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
        } else if (id == R.id.nav_all_sighting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new SightingListFragment()).commit();
        } else if (id == R.id.nav_my_sighting) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.myview_parameter), "myrecords");
            Fragment fragment = new SightingListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
        } *//*else if (id == R.id.nav_about) {
            //startWebViewActivity(getString(R.string.about_us_url), getString(R.string.about_title), false);
            startWebViewActivity("http://biocollect-test.ala.org.au/bioActivity/create/d57961a1-517d-42f2-8446-c373c0c59579", getString(R.string.about_title), true);
        }*//* else if (id == R.id.nav_contact) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.CONTACT_US);
            Intent intent = new Intent(this, SingleFragmentActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * hides the floating button if its not hidden
     */
    @Override
    public void hideFloatingButton() {
        if (fab.getScaleX() != 0.0f)
            fab.animate().scaleX(0.0f).scaleY(0.0f).setDuration(100).setInterpolator(new AccelerateInterpolator()).start();
    }

    /**
     * shows the floating button if its not shown
     */
    @Override
    public void showFloatingButton() {
        if (fab.getScaleX() != 1.0f)
            fab.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    /**
     * floating button onclick listner
     *
     * @param onClickListener
     */
    @Override
    public void setFloatingButtonClickListener(View.OnClickListener onClickListener) {
        fab.setOnClickListener(onClickListener);
    }

    /**
     * shows a message in using Snackbar
     *
     * @param string
     */
    @Override
    public void showSnackBarMessage(String string) {
        showSnackBarMessage(coordinatorLayout, string);
    }

    /**
     * shows a message in using Snackbar
     *
     * @param string
     */
    @Override
    public void showMultiLineSnackBarMessage(String string) {
        showMultiLineSnackBarMessage(coordinatorLayout, string);
    }

    @Override
    public void showSnackBarFromTop(String str) {
        TSnackbar snackbar = TSnackbar.make(coordinatorLayout, str, TSnackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.ala_dark_background));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    /**
     * handle the error and show the error message to the user
     *
     * @param e
     * @param code    http response code to check
     * @param message message to show for the response code
     */
    @Override
    public void handleError(Throwable e, int code, String message) {
        handleError(coordinatorLayout, e, code, message);
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    public void setDrawerMenuChecked(int menuRes) {
        navigationView.getMenu().findItem(menuRes).setChecked(true);
    }

    @Override
    public void setDrawerMenuClicked(int menuRes) {
        onNavigationItemSelected(navigationView.getMenu().findItem(menuRes));
    }
}
