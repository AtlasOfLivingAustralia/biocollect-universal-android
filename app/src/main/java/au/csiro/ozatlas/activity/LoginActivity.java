package au.csiro.ozatlas.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.auth0.android.jwt.JWT;
import androidx.test.espresso.IdlingResource;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

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
    private AuthState mAuthState;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Initializing Authentication
        mAuthService = new AuthorizationService(this);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this.handleLoginResponse());

        CircularProgressButton btn = (CircularProgressButton) findViewById(R.id.loginButton);
        btn.startAnimation();

        // Retrieve the cognito configuration
        boolean cognitoEnabled = getString(R.string.cognito_enabled).equals("true");
        String cognitoRegion = getString(R.string.cognito_region);
        String cognitoUserPool = getString(R.string.cognito_user_pool);

        String discoveryUrl = cognitoEnabled ?
                String.format("https://cognito-idp.%s.amazonaws.com/%s_%s", cognitoRegion, cognitoRegion, cognitoUserPool) :
                getString(R.string.cas_oidc_discovery);

        // Fetch the OIDC discovery document from the issuer
        AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse(discoveryUrl),
                (serviceConfig, ex) -> {
                    if (ex != null) {
                        handleError(coordinatorLayout, ex, 400, getString(R.string.discovery_error));
                        Log.e(TAG, Log.getStackTraceString(ex));
                        return;
                    }

                    // Hack-in the cognito /logout endpoint, as it is not supplied
                    // via end_session_endpoint (/logout isn't OIDC compliant anyway)
                    if (serviceConfig.endSessionEndpoint == null) {
                        try {
                            JSONObject configJson = serviceConfig.toJson();
                            String endSessionEndpoint = serviceConfig.tokenEndpoint
                                    .toString()
                                    .replaceFirst("oauth2/token", "logout");

                            // Append the end_session_endpoint to the JSON object
                            configJson.put("endSessionEndpoint", endSessionEndpoint);
                            configJson.getJSONObject("discoveryDoc").put("end_session_endpoint", endSessionEndpoint);

                            mAuthState = new AuthState(AuthorizationServiceConfiguration.fromJson(configJson));
                            sharedPreferences.writeAuthState(mAuthState);
                        } catch (JSONException e) {
                            handleError(coordinatorLayout, ex, 400, getString(R.string.discovery_error));
                            Log.e(TAG, Log.getStackTraceString(ex));
                            return;
                        }
                    } else {
                        mAuthState = new AuthState(serviceConfig);
                        sharedPreferences.writeAuthState(mAuthState);
                    }
                    btn.revertAnimation();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Login Activity", TAG);
    }

    /**
     * Creates an ActivityResultCallback to handle login responses
     */
    private ActivityResultCallback<ActivityResult> handleLoginResponse() {
        return result -> {
            // Create a new AuthorizationResponse from the resulting intent
            AuthorizationResponse authResp = AuthorizationResponse.fromIntent(result.getData());
            AuthorizationException authEx = AuthorizationException.fromIntent(result.getData());
            mAuthState.update(authResp, authEx);

            boolean cognitoEnabled = getString(R.string.cognito_enabled).equals("true");

            // Ensure that the auth response is valid
            if (authResp != null) {
                showProgressDialog();
                // Exchange the authorization code for access & id tokens
                mAuthService.performTokenRequest(
                        authResp.createTokenExchangeRequest(),
                        (resp, respEx) -> {
                            if (resp != null) { // If the exchange was successful
                                JWT idJwt = new JWT(cognitoEnabled ? resp.idToken : resp.accessToken);

                                // Update the auth state
                                mAuthState.update(resp, respEx);

                                // Update shared preferences
                                sharedPreferences.writeAuthKey(resp.accessToken);
                                sharedPreferences.writeAuthState(mAuthState);
                                sharedPreferences.writeUsername(idJwt.getClaim("email").asString());
                                sharedPreferences.writeUserId(idJwt.getClaim(cognitoEnabled ? "custom:userid" : "userid").asString());
                                sharedPreferences.writeUserDisplayName(
                                        String.format(
                                                "%s %s",
                                                idJwt.getClaim("given_name").asString(),
                                                idJwt.getClaim("family_name").asString()
                                        )
                                );

                                // Navigate to the main activity
                                hideProgressDialog();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                handleError(coordinatorLayout, respEx, 400, getString(R.string.login_error));
                                Log.e(TAG, Log.getStackTraceString(respEx));
                            }
                        });
            } else if (authEx.code != 1 || authEx.type != 0) {
                // If the user didn't cancel the flow, log the error
                Log.e(TAG, Log.getStackTraceString(authEx));
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
        AuthorizationRequest loginRequest =
                new AuthorizationRequest.Builder(
                        mAuthState.getAuthorizationServiceConfiguration(),
                        getString(R.string.client_id),
                        ResponseTypeValues.CODE,
                        Uri.parse(String.format("au.org.ala.%s:/signin", BuildConfig.FLAVOR))).build();

        activityResultLauncher.launch(
                mAuthService.getAuthorizationRequestIntent(loginRequest)
        );
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
