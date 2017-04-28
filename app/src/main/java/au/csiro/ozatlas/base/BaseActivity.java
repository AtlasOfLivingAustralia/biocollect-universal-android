package au.csiro.ozatlas.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.LoginActivity;
import au.csiro.ozatlas.activity.SingleFragmentActivity;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import au.csiro.ozatlas.rest.RestClient;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseActivity extends AppCompatActivity implements BaseActivityFragmentListener, RestClientListener {
    @Inject
    protected AtlasSharedPreferenceManager sharedPreferences;

    @Inject
    protected RestClient restClient;

    private ProgressDialog mProgressDialog;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OzAtlasApplication.component().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //AtlasManager.eventBus.register(this);
        if (!(this instanceof LoginActivity) && sharedPreferences.getAuthKey().equals("")) {
            launchLoginActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
        //AtlasManager.eventBus.unregister(this);
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
     * show the spinner dialog
     */
    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog = new ProgressDialog(this, R.style.OSSProgressBarTheme);
            mProgressDialog.getWindow().setDimAmount(0.2f);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * hide the spinner
     */
    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    /**
     * @param editText to check the empty content
     * @return
     */
    @Override
    public boolean validate(EditText editText) {
        return editText.getText().toString().length() > 0;
    }

    /**
     * @param coordinatorLayout
     * @param string message to show
     */
    @Override
    public void showSnackBarMessage(CoordinatorLayout coordinatorLayout, String string) {
        Snackbar.make(coordinatorLayout, string, Snackbar.LENGTH_INDEFINITE).show();
    }

    /**
     * launch Login Activity from anywhere
     */
    @Override
    public void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * @param url   for the webview fragment
     * @param title activity title
     */
    @Override
    public void startWebViewActivity(String url, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.url_parameter), url);
        bundle.putString(getString(R.string.title_parameter), title);
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    @Override
    public RestClient getRestClient() {
        return restClient;
    }
}
