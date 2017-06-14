package au.csiro.ozatlas.base;

/**
 * Created by sad038 on 12/4/17.
 */

/**
 * Thsi interface is used to communicate with
 * the MainActivity from BaseFragment
 */
public interface MainActivityFragmentListener {
    void hideFloatingButton();

    void showFloatingButton();

    void showSnackBarMessage(String string);

    void handleError(Throwable e, int code, String message);

    void setTitle(String title);
}
