package fragments;

import android.content.Context;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import activity.BilbyBlitzActivityListener;
import activity.BilbyBlitzBaseActivity;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.manager.Language;
import io.realm.Realm;

/**
 * Created by sad038 on 8/11/17.
 */

public abstract class BilbyBlitzBaseFragment extends BaseFragment implements BilbyBlitzActivityListener {
    protected BilbyBlitzActivityListener bilbyBlitzActivityListener;
    protected Realm realm;

    protected abstract void setLanguageValues(Language language);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BilbyBlitzBaseActivity) {
            bilbyBlitzActivityListener = (BilbyBlitzBaseActivity) context;
        }
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
        if (bilbyBlitzActivityListener != null)
            return bilbyBlitzActivityListener.localisedString(key, defaultRes);
        return null;
    }

    /**
     * localised a string if a translation is being chosen
     *
     * @param key
     * @return
     */
    @Override
    public String localisedString(String key) {
        if (bilbyBlitzActivityListener != null)
            return bilbyBlitzActivityListener.localisedString(key);
        return null;
    }

    /**
     * loading the language file
     *
     * @param fileName
     */
    @Override
    public void loadLanguageFile(String fileName, Language language) {
        if (bilbyBlitzActivityListener != null)
            bilbyBlitzActivityListener.loadLanguageFile(fileName, language);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Language language) {
        setLanguageValues(language);
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
        if (realm != null)
            realm.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
