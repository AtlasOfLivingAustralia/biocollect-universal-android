package activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.base.MainActivityFragmentListener;
import au.csiro.ozatlas.fragments.WebViewFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.AddSightingFragment;

/**
 * Created by sad038 on 21/4/17.
 */

/**
 * This is a generic class where a single Fragment can be launched.
 */

public class SingleFragmentActivity extends BaseActivity implements MainActivityFragmentListener {
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

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
                case EDIT_FRAGMENT:
                    fragment = new AddSightingFragment();
                    break;
            }
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
        }
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

    @Override
    public void hideFloatingButton() {

    }

    @Override
    public void showFloatingButton() {

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

    /**
     * Type of Fragments
     */
    public enum FragmentType {
        WEB_FRAGMENT,
        EDIT_FRAGMENT
    }
}
