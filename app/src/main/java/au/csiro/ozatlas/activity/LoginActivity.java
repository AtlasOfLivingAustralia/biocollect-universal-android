package au.csiro.ozatlas.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.JsonObject;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.manager.AtlasManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sad038 on 6/4/17.
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        editUsername.setText(sharedPreferences.getUsername());

        //test code
        if (AtlasManager.isTesting) {
            editUsername.setText("sadat.sadat@csiro.au");
            editPassword.setText("");
        }
    }

    private void postLogin(final String username, String password) {
        showProgressDialog();
        mCompositeDisposable.add(restClient.getService().login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject value) {
                        if (value.has("authKey")) {
                            String authKey = value.get("authKey").getAsString();
                            sharedPreferences.writeAuthKey(authKey);
                            sharedPreferences.writeUsername(username);
                        }
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        showSnackBarMessage(coordinatorLayout, e.getMessage());
                        hideProgressDialog();
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

    @OnClick(R.id.loginButton)
    void loginButton() {
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

    @OnClick(R.id.registerLabel)
    void registerLabel() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.register_url)));
        startActivity(browserIntent);
    }
}
