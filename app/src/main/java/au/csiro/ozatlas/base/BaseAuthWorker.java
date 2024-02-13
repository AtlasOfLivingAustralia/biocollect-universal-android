package au.csiro.ozatlas.base;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import javax.inject.Inject;
import net.openid.appauth.*;

import application.CsiroApplication;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;

public class BaseAuthWorker extends Worker {
    protected final String TAG = getClass().getSimpleName();
    @Inject
    protected AtlasSharedPreferenceManager sharedPreferences;
    private AuthorizationService authService;

    public BaseAuthWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        // Initialize dagger
        CsiroApplication.component().inject(this);

        // Initialize the authorization service
        authService = new AuthorizationService(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        AuthState authState = sharedPreferences.getAuthState();

        // Only perform the token expiry check if authState is valid, and we're authorized
        if (authState != null && authState.isAuthorized()) {
            Log.d(TAG, String.format("Expiration time: %d", authState.getAccessTokenExpirationTime()));
            Log.d(TAG, String.format("Current time: %d", System.currentTimeMillis()));
            Log.d(TAG, String.format("Diff time: %d", authState.getAccessTokenExpirationTime() - System.currentTimeMillis()));

            // Check whether the token is expiring in less than an hour
            if (authState.getAccessTokenExpirationTime() - System.currentTimeMillis() < 3600000) {
                Log.d(TAG, "Less than an hour until the token expires, refreshing...");
                // Refresh the token
                authService.performTokenRequest(
                    authState.createTokenRefreshRequest(),
                    (resp, respEx) -> {
                        // Update the auth state & shared preferences
                        authState.update(resp, respEx);

                        if (respEx == null) {
                            Log.d(TAG, "Token refreshed successfully!");
                            sharedPreferences.writeAuthState(authState);
                            sharedPreferences.writeAuthKey(resp.accessToken);
                        } else {
                            Log.e(TAG, Log.getStackTraceString(respEx.getCause()));
                        }
                    }
                );
            }
        }

        return Result.success();
    }
}