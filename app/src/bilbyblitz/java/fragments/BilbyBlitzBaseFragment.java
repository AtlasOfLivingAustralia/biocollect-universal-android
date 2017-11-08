package fragments;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import au.csiro.ozatlas.base.BaseFragment;

/**
 * Created by sad038 on 8/11/17.
 */

public abstract class BilbyBlitzBaseFragment extends BaseFragment {
    protected abstract void setLanguageValues();

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
