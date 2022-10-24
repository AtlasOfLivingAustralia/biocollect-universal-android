package au.csiro.ozatlas.fragments;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import au.csiro.ozatlas.base.BaseRecyclerWithFooterViewAdapter;
import base.BaseMainActivityFragment;

/**
 * Created by sad038 on 25/5/17.
 */

public abstract class BaseListWithRefreshFragment extends BaseMainActivityFragment implements SwipeRefreshLayout.OnRefreshListener {
    protected final static int MAX = 20;
    protected boolean hasNext = true;
    protected LinearLayoutManager mLayoutManager;
    protected BaseRecyclerWithFooterViewAdapter adapter;
    private int offset = 0;
    private int preLast;

    /**
     * recyclerview scroll listner for
     * pagination. Ifthe last item is shown then the recyclerview shows an
     * footer and make the network call for the next page.
     */
    protected RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            final int lastItem = firstVisibleItemPosition + visibleItemCount;
            if (lastItem == totalItemCount && preLast != lastItem) {
                preLast = lastItem;
                if (hasNext) {
                    adapter.setNeedFooter(true);
                    adapter.notifyDataSetChanged();
                    offset = offset + MAX;
                    fetchItems(offset);
                }
            }
        }
    };

    protected abstract void fetchItems(final int offset);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
    }

    /**
     * reset every data to init
     */
    private void reset() {
        hasNext = true;
        offset = 0;
        fetchItems(offset);
    }

    /**
     * refresh for swipetorefresh layout
     */
    @Override
    public void onRefresh() {
        reset();
    }
}
