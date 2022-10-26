package au.csiro.ozatlas.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.test.espresso.IdlingResource;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import au.csiro.ozatlas.BuildConfig;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import activity.MainActivity;
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

import net.openid.appauth.*;
import net.openid.appauth.AuthState.AuthStateAction;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sad038 on 6/4/17.
 */


/**
 * This Activity is to facilitate the Login
 * functionality for the users
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private EcoDataApiService ecoDataApiService;
    private AuthorizationService mAuthService;
    private AuthorizationServiceConfiguration mAuthServiceConfig;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    //CountingIdlingResource countingIdlingResource = new CountingIdlingResource("LOGIN_CALL");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // REST client service
        ecoDataApiService = new NetworkClient(getString(R.string.ecodata_url)).getRetrofit().create(EcoDataApiService.class);

        // Setting the id of previous successful logged in user
        // sharedPreferences.getUsername()

        // Initializing Authentication
        mAuthService = new AuthorizationService(this);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            AuthorizationResponse authResp = AuthorizationResponse.fromIntent(result.getData());
            if (authResp != null) {
                mAuthService.performTokenRequest(
                        authResp.createTokenExchangeRequest(),
                        (resp, ex) -> {
                            if (resp != null) { // If the exchange was successful
                                Log.d(TAG, String.format("Successful exchange! %s %s", resp.tokenType, resp.accessToken));
                            } else {
                                handleError(coordinatorLayout, ex, 400, getString(R.string.login_error));
                                Log.d(TAG, Log.getStackTraceString(ex));
                            }
                        });
            }
        });

        CircularProgressButton btn = (CircularProgressButton) findViewById(R.id.loginButton);
        btn.startAnimation();

        AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse(getString(R.string.oidc_discovery_url)),
                (serviceConfiguration, ex) -> {
                    if (ex != null) {
                        Log.e(TAG, "failed to fetch configuration");
                        return;
                    }
                    Log.d(TAG, serviceConfiguration.toJsonString());
                    mAuthServiceConfig = serviceConfiguration;
                    btn.revertAnimation();
                });

        // countingIdlingResource.increment();
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
                        //countingIdlingResource.decrement();
                        sharedPreferences.writeAuthKey(value.authKey);
                        sharedPreferences.writeUserDisplayName((value.firstName + " " + value.lastName).trim());
                        sharedPreferences.writeUsername(username);
                        sharedPreferences.writeUserId(value.userId);
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        //countingIdlingResource.decrement();
                        Log.d(TAG, "onError", e);
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
//        if (getValidated())
//            postLogin(editUsername.getText().toString(), editPassword.getText().toString());
        Boolean isDebug = false;
        AuthorizationRequest authRequest =
                new AuthorizationRequest.Builder(
                        mAuthServiceConfig,
                        String.format(isDebug ? "oidc-expo-test" : "%s-mobile-auth-%s", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE),
                        ResponseTypeValues.CODE,
                        Uri.parse(String.format("au.org.ala.auth:/%s/signin", BuildConfig.FLAVOR))).build();

        Log.d(TAG, String.format(
                "%s | %s",
                String.format("%s-mobile-auth-%s", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE),
                String.format("au.org.ala.auth:/%s/signin", BuildConfig.FLAVOR))
        );

        Intent authIntent = mAuthService.getAuthorizationRequestIntent(authRequest);
        activityResultLauncher.launch(authIntent);
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
        return null;//countingIdlingResource;
    }
}
