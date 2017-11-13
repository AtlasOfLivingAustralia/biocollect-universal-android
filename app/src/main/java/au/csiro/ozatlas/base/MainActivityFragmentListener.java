package au.csiro.ozatlas.base;

/**
 * Created by sad038 on 12/4/17.
 */

import android.view.View;

/**
 * Thsi interface is used to communicate with
 * the MainActivity from BaseFragment
 */
public interface MainActivityFragmentListener {
    void hideFloatingButton();

    void showFloatingButton();

    void setFloatingButtonClickListener(View.OnClickListener onClickListener);

    void showSnackBarMessage(String string);

    void showSnackBarFromTop(String str);

    void handleError(Throwable e, int code, String message);

    void setTitle(String title);

    void setDrawerMenuChecked(int menuRes);

    void setDrawerMenuClicked(int menuRes);

    void showMultiLineSnackBarMessage(String string);
}
