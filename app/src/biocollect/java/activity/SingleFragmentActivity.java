package activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.base.MainActivityFragmentListener;
import au.csiro.ozatlas.fragments.WebViewFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.ContactUsFragment;
import fragments.SightingListFragment;

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState==null) {
            Fragment fragment = null;
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                FragmentType fragmentType = (FragmentType) bundle.getSerializable(getString(R.string.fragment_type_parameter));
                setTitle(bundle.getString(getString(R.string.title_parameter), getString(R.string.title_activity_main)), true);
                switch (fragmentType) {
                    case WEB_FRAGMENT:
                        fragment = new WebViewFragment();
                        break;
                    case RECORD_LIST:
                        fragment = new SightingListFragment();
                        break;
                    case CONTACT_US:
                        fragment = new ContactUsFragment();
                        break;
                }
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
            }
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

    @Override
    public void setFloatingButtonClickListener(View.OnClickListener onClickListener) {

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
        Snackbar snackbar = Snackbar.make(coordinatorLayout, string, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    /**
     * Type of Fragments
     */
    public enum FragmentType {
        WEB_FRAGMENT,
        RECORD_LIST,
        CONTACT_US
    }
}
