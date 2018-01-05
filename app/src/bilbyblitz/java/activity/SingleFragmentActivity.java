package activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.MainActivityFragmentListener;
import au.csiro.ozatlas.fragments.WebViewFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.addtrack.AddTrackFragment;
import fragments.addtrack.animal.AddAnimalFragment;
import fragments.addtrack.country.FoodPlantSelectionFragment;
import fragments.offline_species.AvailableSpeciesFragment;
import fragments.offline_species.ExploreSpeciesFragment;
import fragments.offline_species.SearchAndAddFragment;
import fragments.offline_species.SpeciesGroupFragment;
import au.csiro.ozatlas.manager.Language;
import fragments.setting.ProjectListFragment;

/**
 * Created by sad038 on 21/4/17.
 */

/**
 * This is a generic class where a single Fragment can be launched.
 */

public class SingleFragmentActivity extends BilbyBlitzBaseActivity implements MainActivityFragmentListener {
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void setLanguageValues(Language language) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_single_fragment);
        ButterKnife.bind(this);

        Fragment fragment = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FragmentType fragmentType = (FragmentType) bundle.getSerializable(getString(R.string.fragment_type_parameter));
            setTitle(bundle.getString(getString(R.string.title_parameter), getString(R.string.title_activity_main)), true);
            switch (fragmentType) {
                case WEB_FRAGMENT:
                    fragment = new WebViewFragment();
                    break;
                case AVAILABLE_SPECIES:
                    fragment = new AvailableSpeciesFragment();
                    break;
                case SEARCH_ADD_SPECIES:
                    fragment = new SearchAndAddFragment();
                    break;
                case MAP_SPECIES:
                    fragment = new ExploreSpeciesFragment();
                    break;
                case GROUP_SPECIES:
                    fragment = new SpeciesGroupFragment();
                    break;
                case ADD_ANIMAL:
                    fragment = new AddAnimalFragment();
                    break;
                case PROJECT_SELECTION:
                    fragment = new ProjectListFragment();
                    break;
                case EDIT_TRACK_FRAGMENT:
                    fragment = new AddTrackFragment();
                    break;
                case FOOD_PLANT:
                    fragment = new FoodPlantSelectionFragment();
                    break;
            }
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
        }

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());
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
     * setting the title
     * also showing the back button depending on the @param homeButton
     *
     * @param str
     * @param homeButton
     */
    public void setTitle(String str, boolean homeButton) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(homeButton);
            getSupportActionBar().setTitle(str);
        } else {
            setTitle(str);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

    }

    @Override
    public void setDrawerMenuClicked(int menuRes) {

    }

    @Override
    public void showMultiLineSnackBarMessage(String string) {
        showMultiLineSnackBarMessage(coordinatorLayout, string);
    }

    /**
     * Type of Fragments
     */
    public enum FragmentType {
        WEB_FRAGMENT,
        AVAILABLE_SPECIES,
        SEARCH_ADD_SPECIES,
        MAP_SPECIES,
        PROJECT_SELECTION,
        GROUP_SPECIES,
        ADD_ANIMAL,
        CONTACT_US,
        EDIT_TRACK_FRAGMENT,
        FOOD_PLANT
    }
}
