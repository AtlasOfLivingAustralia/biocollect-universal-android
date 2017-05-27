package base;

import android.content.Context;

import activity.MainActivity;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.base.MainActivityFragmentListener;

/**
 * Created by sad038 on 5/4/17.
 */

public class BaseMainActivityFragment extends BaseFragment implements MainActivityFragmentListener {
    protected MainActivityFragmentListener mainActivityFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivityFragmentListener = (MainActivity) context;
        }
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
}
