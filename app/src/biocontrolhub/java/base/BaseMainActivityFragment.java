package base;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import activity.MainActivity;
import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.base.MainActivityFragmentListener;

import static au.csiro.ozatlas.R.id.coordinatorLayout;

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
        } else if (context instanceof SingleFragmentActivity) {
            mainActivityFragmentListener = (SingleFragmentActivity) context;
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
        if (mainActivityFragmentListener != null) {
            mainActivityFragmentListener.showSnackBarMessage(string);
        }
    }

    @Override
    public void showSnackBarFromTop(String str) {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.showSnackBarFromTop(str);
    }

    @Override
    public void handleError(Throwable e, int code, String message) {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.handleError(e, code, message);
    }

    @Override
    public void setTitle(String title) {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.setTitle(title);
    }

    @Override
    public void setDrawerMenuChecked(int menuRes) {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.setDrawerMenuChecked(menuRes);
    }
}
