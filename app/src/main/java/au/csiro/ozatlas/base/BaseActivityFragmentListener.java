package au.csiro.ozatlas.base;

import android.support.design.widget.CoordinatorLayout;
import android.widget.EditText;

/**
 * Created by sad038 on 10/4/17.
 */

/**
 * This interface is to communicate from
 * BaseFragment to the BaseActivity
 */
public interface BaseActivityFragmentListener {
    void showProgressDialog();

    void showProgressDialog(boolean isCancelable);

    void hideProgressDialog();

    boolean validate(EditText editText);

    void launchLoginActivity();

    void startWebViewActivity(String url, String title, boolean chromeClientNeed);

    void showToast(String str);

    void handleError(CoordinatorLayout coordinatorLayout, Throwable e, int code, String message);

    void sendAnalyticsScreenName(String name, String className);
}
