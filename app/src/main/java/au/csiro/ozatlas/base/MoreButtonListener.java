package au.csiro.ozatlas.base;

import android.view.View;

/**
 * Created by sad038 on 16/5/17.
 */

/**
 * More menu click listener to show a pop up menu
 * from the recyclerview items
 */
public interface MoreButtonListener {
    void onPopupMenuClick(View view, int position);
}
