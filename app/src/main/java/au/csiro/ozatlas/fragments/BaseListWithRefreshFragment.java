package au.csiro.ozatlas.fragments;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.BaseRecyclerWithFooterViewAdapter;
import au.csiro.ozatlas.base.BaseFragment;
import base.BaseMainActivityFragment;

/**
 * Created by sad038 on 25/5/17.
 */

public abstract class BaseListWithRefreshFragment extends BaseMainActivityFragment implements SwipeRefreshLayout.OnRefreshListener{
    protected final static int MAX = 20;

    private MenuItem searchMenu;
    private String searchTerm;
    private int offset = 0;
    private int preLast;
    protected boolean hasNext = true;
    private boolean isSearched = false;
    protected LinearLayoutManager mLayoutManager;
    protected BaseRecyclerWithFooterViewAdapter adapter;

    protected abstract void fetchItems(String searchTerm, final int offset);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        /**
         * search layout setup
         */
        searchMenu = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTerm = query;
                searchView.clearFocus();
                offset = 0;
                hasNext = true;
                isSearched = true;
                fetchItems(searchTerm, offset);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchTerm = newText;
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenu, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (isSearched) {
                    reset();
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * reset every data to init
     */
    private void reset() {
        searchTerm = null;
        hasNext = true;
        offset = 0;
        isSearched = false;
        fetchItems(null, offset);
    }

    /**
     * refresh for swipetorefresh layout
     */
    @Override
    public void onRefresh() {
        if (searchMenu.isActionViewExpanded())
            searchMenu.collapseActionView();
        reset();
    }

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
                    fetchItems(searchTerm, offset);
                }
            }
        }
    };
}
