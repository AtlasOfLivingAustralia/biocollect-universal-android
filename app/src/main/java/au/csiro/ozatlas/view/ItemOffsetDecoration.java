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

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffsetLeft, mItemOffsetRight, mItemOffsetTop, mItemOffsetBottom;

    public ItemOffsetDecoration(int itemOffset) {
        mItemOffsetLeft = mItemOffsetRight = mItemOffsetTop = mItemOffsetBottom = itemOffset;
    }

    public ItemOffsetDecoration(int itemOffsetH, int itemOffsetV) {
        mItemOffsetLeft = mItemOffsetRight = itemOffsetH;
        mItemOffsetTop = mItemOffsetBottom = itemOffsetV;
    }

    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

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
