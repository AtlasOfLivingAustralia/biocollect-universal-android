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

public class BaseFragment extends Fragment implements BaseActivityFragmentListener {
    @Inject
    AtlasSharedPreferenceManager sharedPreferences;

    protected BaseActivityFragmentListener baseActivityFragmentListener;
    protected FloatingActionButtonListener floatingActionButtonListener;
    protected RestClientListener restClientListener;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    protected RestClient restClient;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OzAtlasApplication.component().inject(this);
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
            floatingActionButtonListener = (MainActivity) context;
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
    public void showSnackBarMessage(CoordinatorLayout coordinatorLayout, String string) {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.showSnackBarMessage(coordinatorLayout, string);
    }

    @Override
    public void launchLoginActivity() {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.launchLoginActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
