package au.csiro.ozatlas.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.auth0.android.jwt.JWT;

import net.openid.appauth.*;

import java.util.HashMap;

import activity.MainActivity;
import au.csiro.ozatlas.BuildConfig;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.LoginActivity;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseSettingsFragment extends BaseMainActivityFragment {

    @BindView(R.id.logout_settings_small_text)
    TextView logoutSmallText;
    @BindView(R.id.logout_settings_button)
    View logoutButton;

    private AuthorizationService mAuthService;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        hideFloatingButton();

        // Initialize a new AuthorizationService for logging out
        mAuthService = new AuthorizationService(this.getContext());
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this.handleLogoutResponse());

        logoutSmallText.setText(getLoggedInShortMessage());
        configureLogoutButton(logoutButton);

        return view;
    }

    protected void configureLogoutButton(View logoutButton) {
        logoutButton.setOnClickListener(logoutView ->
                {
                    AtlasDialogManager.alertBox(
                            getActivity(),
                            getString(R.string.logout_message),
                            getString(R.string.logout_title),
                            getString(R.string.logout_title),
                            (dialog, which) -> handleLogout()
                    );
                }
        );
    }

    private String getLoggedInShortMessage() {
        return getString(R.string.logged_in_message, sharedPreferences.getUsername());
    }

    protected void handleLogout() {
        AuthState authState = sharedPreferences.getAuthState();

        // Ensure that an authorization service config is supplied
        if (authState.getAuthorizationServiceConfiguration() == null) {
            launchLoginActivity();
        } else {
            Log.d(TAG, "AuthorizationServiceConfig is NOT NULL");
            performLogoutRequest();
        }
    }

    private void performLogoutRequest() {
        AuthState authState = sharedPreferences.getAuthState();

        // Create a hashmap for the additional parameters
        String redirectUri = String.format("au.org.ala.%s:/signout", BuildConfig.FLAVOR);
        HashMap<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("client_id", getString(R.string.oidc_client_id));
        additionalParams.put("logout_uri", redirectUri);

        // Build the logout request
        EndSessionRequest.Builder logoutRequest = new EndSessionRequest.Builder(authState.getAuthorizationServiceConfiguration())
                .setPostLogoutRedirectUri(Uri.parse(redirectUri))
                .setAdditionalParameters(additionalParams)
                .setState(null);

        Log.d(TAG, String.format("LOGOUT URI: %s", logoutRequest.build().toUri().toString()));

        // Launch the logout request
        activityResultLauncher.launch(
                mAuthService.getEndSessionRequestIntent(logoutRequest.build())
        );
    }

    /**
     * Creates an ActivityResultCallback to handle login responses
     */
    private ActivityResultCallback<ActivityResult> handleLogoutResponse() {
        return result -> {
            EndSessionResponse authResp = EndSessionResponse.fromIntent(result.getData());

            // Ensure that the logout request was successful
            if (authResp != null) {
                Log.d(TAG, authResp.jsonSerializeString());
                launchLoginActivity();
            } else {
                AuthorizationException authEx = AuthorizationException.fromIntent(result.getData());
                AtlasDialogManager.alertBox(
                        getActivity(),
                        getString(R.string.logout_message),
                        getString(R.string.logout_title),
                        getString(R.string.logout_title),
                        (dialog, which) -> handleLogout()
                );
                AtlasDialogManager.alertBoxForMessage(getActivity(), authEx.getMessage(), "OK");
            }
        };
    }
}
