package au.csiro.ozatlas.base;

import android.support.design.widget.CoordinatorLayout;

/**
 * Created by sad038 on 12/4/17.
 */

public interface MainActivityFragmentListener {
    void hideFloatingButton();

    void showFloatingButton();

    void showSnackBarMessage(String string);

    void handleError(Throwable e, int code, String message);
}
