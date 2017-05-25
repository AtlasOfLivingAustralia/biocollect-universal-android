package au.csiro.ozatlas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sad038 on 25/5/17.
 */

public abstract class BaseRecyclerWithFooterViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final int FOOTER = 2;
    protected final int NORMAL = 1;
    protected boolean needFooter;

    /**
     * if we want to show the Footer
     *
     * @param needFooter
     */
    public void setNeedFooter(boolean needFooter) {
        this.needFooter = needFooter;
    }
}
