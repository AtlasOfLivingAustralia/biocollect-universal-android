package au.csiro.ozatlas.login;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.rest.RestClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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

    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mCompositeDisposable = new CompositeDisposable();
        postLogin();
    }

    private void postLogin(){
        restClient.getService().register()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(String value) {
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @OnClick(R.id.loginButton)
    void loginButton(){

    }

    @OnClick(R.id.registerLabel)
    void registerLabel(){

    }
}
