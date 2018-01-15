package au.csiro.ozatlas.base;

import android.app.IntentService;

import javax.inject.Inject;

import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import au.csiro.ozatlas.rest.RestClient;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by sad038 on 18/10/17.
 */

public abstract class BaseIntentService extends IntentService {
    protected final String TAG = getClass().getSimpleName();
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Inject
    protected AtlasSharedPreferenceManager sharedPreferenceManager;

    @Inject
    protected RestClient restClient;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseIntentService(String name) {
        super(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
