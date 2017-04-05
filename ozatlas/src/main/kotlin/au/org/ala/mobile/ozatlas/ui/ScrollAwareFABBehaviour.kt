package au.org.ala.mobile.ozatlas.ui

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import timber.log.Timber

/**
 * Work around for an illegal argument exception thrown by the default FAB.Behaviour in v25.0.0 of
 * the support library.
 */
class ScrollAwareFABBehaviour(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior(context, attrs) {

    override fun getInsetDodgeRect(parent: CoordinatorLayout, child: FloatingActionButton, rect: Rect): Boolean {
        val getInsetDodgeRect = super.getInsetDodgeRect(parent, child, rect)
        Timber.v("getInsetDodgeRect $getInsetDodgeRect")
        return false
    }
}