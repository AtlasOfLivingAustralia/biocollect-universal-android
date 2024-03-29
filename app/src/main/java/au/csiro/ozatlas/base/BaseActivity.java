package au.csiro.ozatlas.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import net.openid.appauth.AuthState;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import activity.SingleFragmentActivity;
import application.CsiroApplication;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.LoginActivity;
import au.csiro.ozatlas.fragments.WebViewFragment;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import au.csiro.ozatlas.rest.RestClient;
import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseActivity extends AppCompatActivity implements BaseActivityFragmentListener, FragmentManager.OnBackStackChangedListener {
    public final static int REQUEST_WEBVIEW = 99;
    protected final String TAG = getClass().getSimpleName();
    @Inject
    protected AtlasSharedPreferenceManager sharedPreferences;
    @Inject
    protected RestClient restClient;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected Realm realm;
    @Inject
    FirebaseAnalytics firebaseAnalytics;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializing dagger
        CsiroApplication.component().inject(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getApplicationContext().getPackageName().equals("au.org.ala.mobile.ozatlas") || getApplicationContext().getPackageName().equals("au.org.ala.bilbyblitz"))
            realm = Realm.getDefaultInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        //checking the authkey from sharedpreference. Launch LoginActivity in case there is not key
        AuthState authState = sharedPreferences.getAuthState();
        if (!(this instanceof LoginActivity) && (sharedPreferences.getAuthKey().equals("") || authState == null || authState.hasClientSecretExpired())) {
            launchLoginActivity();
        }

        Log.d(TAG, "Resuming, performing token refresh check...");
        PeriodicWorkRequest authRefreshWorkRequest = new PeriodicWorkRequest.Builder(BaseAuthWorker.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS).build();
        WorkManager workManager = WorkManager.getInstance(this.getApplicationContext());
        workManager.enqueueUniquePeriodicWork("BaseAuthWorker", ExistingPeriodicWorkPolicy.REPLACE , authRefreshWorkRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    /**
     * handling home button press
     *
     * @param item
     * @return
     */
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
     * show the spinning dialog
     */
    @Override
    public void showProgressDialog() {
        showProgressDialog(false);
    }

    /**
     * spinning dialog
     *
     * @param isCancelable
     */
    @Override
    public void showProgressDialog(boolean isCancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.OSSProgressBarTheme);
            mProgressDialog.getWindow().setDimAmount(0.2f);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(isCancelable);
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * hide the spinning dialog
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
     * @param string            message to show
     */
    public void showSnackBarMessage(CoordinatorLayout coordinatorLayout, String string, int time) {
        Snackbar.make(coordinatorLayout, string, time).show();
    }

    /**
     * @param coordinatorLayout
     * @param string            message to show
     */
    public void showSnackBarMessage(CoordinatorLayout coordinatorLayout, String string) {
        Snackbar.make(coordinatorLayout, string, Snackbar.LENGTH_LONG).show();
    }

    /**
     * @param coordinatorLayout
     * @param string            multiline message to show
     */
    public void showMultiLineSnackBarMessage(CoordinatorLayout coordinatorLayout, String string) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, string, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    /**
     * launch Login Activity from anywhere
     */
    @Override
    public void launchLoginActivity() {
        sharedPreferences.writeAuthKey("");
        sharedPreferences.writeUserId("");
        if (realm != null)
            realm.executeTransactionAsync(realm -> realm.deleteAll());
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
    public void startWebViewActivity(String url, String title, boolean chromeClientNeed) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.url_parameter), url);
        bundle.putString(getString(R.string.title_parameter), title);
        bundle.putBoolean(getString(R.string.chrome_client_need_parameter), chromeClientNeed);

        //if this is an instance of SingleFragmentActivity then use it rather than making another intent
        if (this instanceof SingleFragmentActivity) {
            Fragment fragment = new WebViewFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        } else {
            bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.WEB_FRAGMENT);
            Intent intent = new Intent(this, SingleFragmentActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_WEBVIEW);
        }
    }


    /**
     * show a toast message
     *
     * @param str
     */
    @Override
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * handle the error response from a HTTP call
     *
     * @param coordinatorLayout
     * @param e
     * @param code
     * @param message
     */
    @Override
    public void handleError(CoordinatorLayout coordinatorLayout, Throwable e, int code, String message) {
        if (!TextUtils.isEmpty(message)) {
            //showSnackBarMessage(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
            AtlasDialogManager.alertBoxForMessage(this, message, "OK");
        } else if (e instanceof UnknownHostException) {
            showSnackBarMessage(coordinatorLayout, getString(R.string.not_internet_title), Snackbar.LENGTH_INDEFINITE);
        } else if (e instanceof HttpException && ((HttpException) e).code() == code) {
            showSnackBarMessage(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
        } else if (e instanceof SocketTimeoutException) {
            showSnackBarMessage(coordinatorLayout, e.getMessage(), Snackbar.LENGTH_INDEFINITE);
        } else {
            //showSnackBarMessage(coordinatorLayout, e.getMessage());
            showSnackBarMessage(coordinatorLayout, getString(R.string.generic_error), Snackbar.LENGTH_INDEFINITE);
        }
    }

    @Override
    public void sendAnalyticsScreenName(String name, String className) {
        firebaseAnalytics.setCurrentScreen(this, name, className);
    }

    /**
     * disposing RxDisposable.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null)
            mCompositeDisposable.dispose();
        if (realm != null)
            realm.close();
    }

    /**
     * when the stack of fragments changes
     */
    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    /**
     * Enable Up button only  if there are
     * entries in the back stack
     */
    public void shouldDisplayHomeUp() {
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    /**
     * This method is called when the up button is pressed. Just the pop back stack.
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
