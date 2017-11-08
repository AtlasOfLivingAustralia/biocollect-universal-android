package activity;

import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.manager.FileUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import language.LanguageManager;

/**
 * Created by sad038 on 8/11/17.
 */

public abstract class BilbyBlitzBaseActivity extends BaseActivity {
    protected abstract void setLanguageValues();

    @Override
    public void onResume() {
        super.onResume();
        if (!sharedPreferences.getLanguageFileName().equals("") && LanguageManager.languageJSON == null) {
            //reading the tags from file
            mCompositeDisposable.add(getFileReadObservable(sharedPreferences.getLanguageFileName())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String value) {
                            Log.d(TAG, value);
                            try {
                                LanguageManager.languageJSON = new JSONObject(value);
                                EventBus.getDefault().post(value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        }
    }

    /**
     * Observable to read the language file
     *
     * @return
     */
    private Observable<String> getFileReadObservable(final String filename) {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(FileUtils.readAsset(filename, BilbyBlitzBaseActivity.this));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String json) {
        setLanguageValues();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
