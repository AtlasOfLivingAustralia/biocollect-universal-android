package au.csiro.ozatlas.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;

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

/**
 * Created by sad038 on 6/4/17.
 */


/**
 * This Activity is to facilitate the Login
 * functionlity for the users
 */
public class LoginActivity extends BaseActivity {
    private final String TAG = "LoginActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //rest client service
        ecoDataApiService = new NetworkClient(getString(R.string.ecodata_url)).getRetrofit().create(EcoDataApiService.class);

        //setting the id of previous successful logged in user
        editUsername.setText(sharedPreferences.getUsername());

        //test code
        if (AtlasManager.isTesting) {
            editUsername.setText("sadat.sadat@csiro.au");
            editPassword.setText("password");
        }
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
                        sharedPreferences.writeAuthKey(value.authKey);
                        sharedPreferences.writeUserDisplayName((value.firstName + " " + value.lastName).trim());
                        sharedPreferences.writeUsername(username);
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
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
        if (!validate(editUsername)) {
            inputLayoutUsername.setError(getString(R.string.username_missing_error));
            return;
        }

        if (!validate(editPassword)) {
            inputLayoutPassword.setError(getString(R.string.password_missing_error));
            return;
        }
        postLogin(editUsername.getText().toString(), editPassword.getText().toString());
    }

    /**
     * click event for registration textview
     */
    @OnClick(R.id.registerLabel)
    void registerLabel() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.register_url)));
        startActivity(browserIntent);
    }
}
