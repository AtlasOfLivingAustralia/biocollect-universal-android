package activity;

import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.manager.FileUtils;
import au.csiro.ozatlas.manager.Language;
import fragments.offline_species.service.FetchListSpeciesService;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import language.LanguageManager;

/**
 * Created by sad038 on 8/11/17.
 */

public abstract class BilbyBlitzBaseActivity extends BaseActivity implements BilbyBlitzActivityListener {
    protected abstract void setLanguageValues(Language language);

    @Override
    public void onResume() {
        super.onResume();
        if (!sharedPreferences.getLanguageFileName().equals("") && LanguageManager.languageJSON == null) {
            //reading the language files
            loadLanguageFile(sharedPreferences.getLanguageFileName(), sharedPreferences.getLanguageEnumLanguage());
        }
    }


    /**
     * loading the language file
     *
     * @param fileName
     */
    @Override
    public void loadLanguageFile(String fileName, Language language) {
        showProgressDialog();
        mCompositeDisposable.add(getFileReadObservable(fileName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        Log.d(TAG, value);
                        try {
                            LanguageManager.languageJSON = new JSONObject(value);
                            //EventBus.getDefault().post(value);
                            EventBus.getDefault().post(language);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                    }
                }));
    }

    /**
     * Observable to read the language file
     *
     * @return
     */
    private Observable<String> getFileReadObservable(final String filename) {
        return Observable.defer(() -> Observable.just(FileUtils.readAsset(filename, BilbyBlitzBaseActivity.this)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Language language) {
        setLanguageValues(language);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        startService(new Intent(this, FetchListSpeciesService.class));
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * localised a string if a translation is being chosen
     *
     * @param key
     * @param defaultRes
     * @return
     */
    @Override
    public String localisedString(String key, int defaultRes) {
        return LanguageManager.localizedString(this, key, defaultRes);
    }
}
