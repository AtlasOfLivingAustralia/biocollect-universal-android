package au.csiro.ozatlas.base;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.widget.EditText;

import javax.inject.Inject;

import au.csiro.ozatlas.OzAtlasApplication;
import au.csiro.ozatlas.activity.MainActivity;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import au.csiro.ozatlas.rest.RestClient;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseFragment extends Fragment implements BaseActivityFragmentListener, MainActivityFragmentListener {
    @Inject
    protected AtlasSharedPreferenceManager sharedPreferences;

    protected BaseActivityFragmentListener baseActivityFragmentListener;
    protected MainActivityFragmentListener mainActivityFragmentListener;
    protected RestClientListener restClientListener;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected RestClient restClient;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        OzAtlasApplication.component().inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivityFragmentListener = (BaseActivity) context;
            restClientListener = (BaseActivity) context;
            restClient = restClientListener.getRestClient();
        }
        if (context instanceof MainActivity) {
            mainActivityFragmentListener = (MainActivity) context;
        }
    }

    @Override
    public void showProgressDialog() {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.hideProgressDialog();
    }

    @Override
    public boolean validate(EditText editText) {
        if (baseActivityFragmentListener != null)
            return baseActivityFragmentListener.validate(editText);
        return false;
    }

    @Override
    public void hideFloatingButton() {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.hideFloatingButton();
    }

    @Override
    public void showFloatingButton() {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.showFloatingButton();
    }

    @Override
    public void showSnackBarMessage(String string) {
        if (getActivity() instanceof MainActivity && mainActivityFragmentListener != null) {
            mainActivityFragmentListener.showSnackBarMessage(string);
        }
    }

    @Override
    public void handleError(Throwable e, int code, String message) {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.handleError(e, code, message);
    }

    @Override
    public void launchLoginActivity() {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.launchLoginActivity();
    }

    @Override
    public void startWebViewActivity(String url, String title) {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.startWebViewActivity(url, title);
    }

    @Override
    public void showToast(String str) {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.showToast(str);
    }

    @Override
    public void handleError(CoordinatorLayout coordinatorLayout, Throwable e, int code, String message) {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.handleError(coordinatorLayout, e, code, message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
