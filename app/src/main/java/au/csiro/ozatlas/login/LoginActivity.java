package au.csiro.ozatlas.login;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.rest.RestClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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

    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if(AtlasManager.isTesting){
            editUsername.setText("sadat.sadat@csiro.au");
            editPassword.setText("");
        }
        mCompositeDisposable = new CompositeDisposable();
    }

    private void postLogin(String username, String password){
        showProgressDialog();
        mCompositeDisposable.add(restClient.getService().login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject value) {
                        if(value.has("authKey")){
                            String authKey = value.get("authKey").getAsString();
                            sharedPreferences.writeAuthKey(authKey);
                            showSnackBarMessage(coordinatorLayout, "success");
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
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    @OnClick(R.id.loginButton)
    void loginButton(){
        if(!validate(editUsername)){
            inputLayoutUsername.setError(getString(R.string.username_missing_error));
            return;
        }

        if(!validate(editPassword)){
            inputLayoutPassword.setError(getString(R.string.password_missing_error));
            return;
        }

        postLogin(editUsername.getText().toString(), editPassword.getText().toString());
    }

    @OnClick(R.id.registerLabel)
    void registerLabel(){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
