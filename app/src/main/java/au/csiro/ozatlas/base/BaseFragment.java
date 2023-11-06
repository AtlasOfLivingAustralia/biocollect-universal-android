package au.csiro.ozatlas.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import android.widget.EditText;

import javax.inject.Inject;

import activity.SingleFragmentActivity;
import application.CsiroApplication;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.fragments.WebViewFragment;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;
import au.csiro.ozatlas.rest.RestClient;
import io.reactivex.disposables.CompositeDisposable;

import static au.csiro.ozatlas.base.BaseActivity.REQUEST_WEBVIEW;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseFragment extends Fragment implements BaseActivityFragmentListener {
    protected final String TAG = getClass().getSimpleName();
    @Inject
    protected AtlasSharedPreferenceManager sharedPreferences;
    @Inject
    protected RestClient restClient;
    protected BaseActivityFragmentListener baseActivityFragmentListener;
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CsiroApplication.component().inject(this);
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
        }
    }

    @Override
    public void showProgressDialog() {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.showProgressDialog();
    }

    @Override
    public void showProgressDialog(boolean isCancelable) {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.showProgressDialog(isCancelable);
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
    public void launchLoginActivity() {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.launchLoginActivity();
    }

/*    @Override
    public void startWebViewActivity(String url, String title, boolean chromeClientNeed) {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.startWebViewActivity(url, title, chromeClientNeed);
    }*/


    /**
     * @param url   for the webview fragment
     * @param title activity title
     */
    @Override
    public void startWebViewActivity(String url, String title, boolean chromeClientNeed) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.url_parameter), url);
        bundle.putString(getString(R.string.title_parameter), title);
        bundle.putBoolean(getString(R.string.chrome_client_need_parameter), chromeClientNeed);

        //if this is an instance of SingleFragmentActivity then use it rather than making another intent
        if (getActivity() instanceof SingleFragmentActivity) {
            Fragment fragment = new WebViewFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        } else {
            bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.WEB_FRAGMENT);
            Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_WEBVIEW);
        }
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
    public void sendAnalyticsScreenName(String name, String className) {
        if (baseActivityFragmentListener != null)
            baseActivityFragmentListener.sendAnalyticsScreenName(name, className);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
