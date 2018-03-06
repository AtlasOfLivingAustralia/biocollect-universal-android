package au.csiro.ozatlas.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.IdlingResource;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import activity.MainActivity;
import au.csiro.ozatlas.BuildConfig;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.model.LoginResponse;
import au.csiro.ozatlas.rest.EcoDataApiService;
import au.csiro.ozatlas.rest.NetworkClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import android.support.test.espresso.idling.CountingIdlingResource;

/**
 * Created by sad038 on 6/4/17.
 */


/**
 * This Activity is to facilitate the Login
 * functionlity for the users
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.inputLayoutUsername)
    TextInputLayout inputLayoutUsername;
    @BindView(R.id.editUsername)
    EditText editUsername;
    @BindView(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private EcoDataApiService ecoDataApiService;
    CountingIdlingResource countingIdlingResource = new CountingIdlingResource("LOGIN_CALL");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //rest client service
        ecoDataApiService = new NetworkClient(getString(R.string.ecodata_url)).getRetrofit().create(EcoDataApiService.class);

        //setting the id of previous successful logged in user
        editUsername.setText(sharedPreferences.getUsername());
        editPassword.setImeActionLabel("Login", EditorInfo.IME_ACTION_DONE);
        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginButton();
                }
                return false;
            }
        });
        countingIdlingResource.increment();
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Login Activity", TAG);
    }

    /**
     * make a network call for getting the AuthKey and user display name
     *
     * @param username login username
     * @param password user's password
     */
    private void postLogin(final String username, String password) {
        showProgressDialog();
        mCompositeDisposable.add(ecoDataApiService.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse value) {
                        countingIdlingResource.decrement();
                        sharedPreferences.writeAuthKey(value.authKey);
                        sharedPreferences.writeUserDisplayName((value.firstName + " " + value.lastName).trim());
                        sharedPreferences.writeUsername(username);
                        sharedPreferences.writeUserId(value.userId);
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        countingIdlingResource.decrement();
                        Log.d(TAG, "onError");
                        hideProgressDialog();
                        handleError(coordinatorLayout, e, 400, getString(R.string.login_error));
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    /**
     * click event for login button
     */
    @OnClick(R.id.loginButton)
    void loginButton() {
        AtlasManager.hideKeyboard(this);
        if (getValidated())
            postLogin(editUsername.getText().toString(), editPassword.getText().toString());
    }

    private boolean getValidated() {
        boolean value = true;
        if (!validate(editUsername)) {
            inputLayoutUsername.setError(getString(R.string.username_missing_error));
            value = false;
        } else {
            inputLayoutUsername.setError("");
        }

        if (!validate(editPassword)) {
            inputLayoutPassword.setError(getString(R.string.password_missing_error));
            value = false;
        } else {
            inputLayoutPassword.setError("");
        }
        return value;
    }

    /**
     * click event for registration textview
     */
    @OnClick(R.id.registerLabel)
    void registerLabel() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.register_url)));
        startActivity(browserIntent);
    }

    /**
     * Only called from test
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        return countingIdlingResource;
    }
}
