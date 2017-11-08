package base;

import android.content.Context;
import android.view.View;

import activity.MainActivity;
import activity.SingleFragmentActivity;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.base.MainActivityFragmentListener;
import fragments.BilbyBlitzBaseFragment;

/**
 * Created by sad038 on 5/4/17.
 */

public abstract class BaseMainActivityFragment extends BilbyBlitzBaseFragment implements MainActivityFragmentListener {
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
    public void setFloatingButtonClickListener(View.OnClickListener onClickListener) {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.setFloatingButtonClickListener(onClickListener);
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

    @Override
    public void setDrawerMenuClicked(int menuRes) {
        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.setDrawerMenuClicked(menuRes);
    }
}
