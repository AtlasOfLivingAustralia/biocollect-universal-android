package au.csiro.ozatlas.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * recyclerview's items' decoration
 * spaces around each item
 */
public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffsetLeft, mItemOffsetRight, mItemOffsetTop, mItemOffsetBottom;

    /**
     * constructor
     * @param itemOffset
     */
    public ItemOffsetDecoration(int itemOffset) {
        mItemOffsetLeft = mItemOffsetRight = mItemOffsetTop = mItemOffsetBottom = itemOffset;
    }

    /**
     * constructor
     * @param itemOffsetH
     * @param itemOffsetV
     */
    public ItemOffsetDecoration(int itemOffsetH, int itemOffsetV) {
        mItemOffsetLeft = mItemOffsetRight = itemOffsetH;
        mItemOffsetTop = mItemOffsetBottom = itemOffsetV;
    }

    /**
     * constructor
     * @param context
     * @param itemOffsetId
     */
    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    /**
     * constructor
     * @param context
     * @param itemOffsetIdHorizontal
     * @param itemOffsetIdVertical
     */
    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetIdHorizontal, @DimenRes int itemOffsetIdVertical) {
        this(context.getResources().getDimensionPixelSize(itemOffsetIdHorizontal), context.getResources().getDimensionPixelSize(itemOffsetIdVertical));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffsetLeft, mItemOffsetTop, mItemOffsetRight, mItemOffsetBottom);
    }
}
