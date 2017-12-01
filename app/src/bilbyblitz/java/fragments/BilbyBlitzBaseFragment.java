package fragments;

import android.content.Context;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import activity.BilbyBlitzActivityListener;
import activity.BilbyBlitzBaseActivity;
import au.csiro.ozatlas.base.BaseFragment;
import io.realm.Realm;

/**
 * Created by sad038 on 8/11/17.
 */

public abstract class BilbyBlitzBaseFragment extends BaseFragment implements BilbyBlitzActivityListener {
    protected BilbyBlitzActivityListener bilbyBlitzActivityListener;
    protected Realm realm;

    protected abstract void setLanguageValues();


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
     * loading the language file
     *
     * @param fileName
     */
    @Override
    public void loadLanguageFile(String fileName) {
        if (bilbyBlitzActivityListener != null)
            bilbyBlitzActivityListener.loadLanguageFile(fileName);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
