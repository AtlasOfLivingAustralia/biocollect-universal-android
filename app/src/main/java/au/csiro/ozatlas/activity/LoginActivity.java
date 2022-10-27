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

import com.auth0.android.jwt.JWT;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import net.openid.appauth.*;

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

    private AuthorizationService mAuthService;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Initializing Authentication
        mAuthService = new AuthorizationService(this);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this.handleAuthResponse());

        CircularProgressButton btn = (CircularProgressButton) findViewById(R.id.loginButton);
        btn.startAnimation();

        // Fetch the OIDC discovery document from the issuer
        AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse(getString(R.string.oidc_discovery_url)),
                (serviceConfiguration, ex) -> {
                    if (ex != null) {
                        Log.e(TAG, "failed to fetch configuration");
                        handleError(coordinatorLayout, ex, 400, getString(R.string.discovery_error));
                        Log.e(TAG, Log.getStackTraceString(ex));
                        return;
                    }
                    Log.d(TAG, serviceConfiguration.toJsonString());
                    sharedPreferences.writeAuthServiceConfig(serviceConfiguration);
                    btn.revertAnimation();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Login Activity", TAG);
    }

    /**
     * Creates an ActivityResultCallback to handle authorization responses
     */
    private ActivityResultCallback<ActivityResult> handleAuthResponse() {
        return result -> {
            // Create a new AuthorizationResponse from the resulting intent
            AuthorizationResponse authResp = AuthorizationResponse.fromIntent(result.getData());

            // Ensure that the auth response is valid
            if (authResp != null) {
                showProgressDialog();

                // Exchange the authorization code for access & id tokens
                mAuthService.performTokenRequest(
                        authResp.createTokenExchangeRequest(),
                        (resp, ex) -> {
                            if (resp != null) { // If the exchange was successful
                                JWT idJwt = new JWT(resp.idToken);

                                // Update shared preferences
                                sharedPreferences.writeAuthKey(resp.accessToken);
                                sharedPreferences.writeIdToken(resp.idToken);
                                sharedPreferences.writeUserDisplayName(idJwt.getClaim("name").asString());
                                sharedPreferences.writeUsername(idJwt.getClaim("email").asString());
                                sharedPreferences.writeUserId(idJwt.getClaim("userid").asString());

                                // Navigate to the main activity
                                hideProgressDialog();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                handleError(coordinatorLayout, ex, 400, getString(R.string.login_error));
                                Log.d(TAG, Log.getStackTraceString(ex));
                            }
                        });
            }
        };
    }

    /**
     * click event for login button
     */
    @OnClick(R.id.loginButton)
    void loginButton() {
        AtlasManager.hideKeyboard(this);
//        if (getValidated())
//            postLogin(editUsername.getText().toString(), editPassword.getText().toString());
        Boolean useTestClient = true;
        AuthorizationRequest authRequest =
                new AuthorizationRequest.Builder(
                        sharedPreferences.getAuthServiceConfig(),
                        String.format(useTestClient ? "oidc-expo-test" : "%s-mobile-auth-%s", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE),
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
