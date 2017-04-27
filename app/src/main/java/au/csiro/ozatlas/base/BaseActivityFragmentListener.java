package au.csiro.ozatlas.base;

import android.support.design.widget.CoordinatorLayout;
import android.widget.EditText;

/**
 * Created by sad038 on 10/4/17.
 */

public interface BaseActivityFragmentListener {
    void showProgressDialog();

    void hideProgressDialog();

    boolean validate(EditText editText);

    void showSnackBarMessage(CoordinatorLayout coordinatorLayout, String string);

    void showSnackBarMessage(String string);

    void launchLoginActivity();

    void startWebViewActivity(String url, String title);

    void showToast(String str);
}
