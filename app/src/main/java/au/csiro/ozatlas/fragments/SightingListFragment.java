package au.csiro.ozatlas.fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SightAdapter;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.listener.RecyclerItemClickListener;
import au.csiro.ozatlas.model.Sight;
import au.csiro.ozatlas.model.SightList;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sad038 on 13/4/17.
 */

public class SightingListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "SightingListFragment";
    private final static int MAX = 20;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;


    private SightAdapter sightAdapter;
    private List<Sight> sights = new ArrayList<>();
    private String myRecords;
    private MenuItem searchMenu;
    private String searchTerm;
    private int offset = 0;
    private int preLast;
    private int totalSighting;
    private boolean hasNext = true;
    private boolean isSearched = false;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sight_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        mainActivityFragmentListener.showFloatingButton();

        //for my sighting
        Bundle bundle = getArguments();
        if (bundle != null) {
            myRecords = bundle.getString(getString(R.string.myview_parameter));
        }

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        sightAdapter = new SightAdapter(sights);
        recyclerView.setAdapter(sightAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        startWebViewActivity(getString(R.string.sighting_detail_url, sights.get(position).activityId), getString(R.string.sight_detail));
                    }
                })
        );

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting
        getSightings(null, 0);

        return view;
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
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
                    sightAdapter.setNeedFooter(true);
                    sightAdapter.notifyDataSetChanged();
                    offset = offset + MAX;
                    getSightings(searchTerm, offset);
                }
            }
        }
    };

    private void getSightings(String searchTerm, final int offset) {
        if (offset == 0)
            swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(restClient.getService().getSightings(getString(R.string.project_id), MAX, offset, true, myRecords, searchTerm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SightList>() {
                    @Override
                    public void onNext(SightList value) {
                        if(value!=null && value.total!=null) {
                            totalSighting = value.total;
                            if (offset == 0)
                                sights.clear();
                            if (sights.size() == value.total) {
                                hasNext = false;
                            } else {
                                sights.addAll(value.activities);
                            }
                            sightAdapter.setNeedFooter(false);
                            sightAdapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        showSnackBarMessage(coordinatorLayout, e.getMessage());
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        total.setText(getString(R.string.total_sighting, totalSighting));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
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
                getSightings(searchTerm, offset);
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

    private void reset() {
        searchTerm = null;
        hasNext = true;
        offset = 0;
        isSearched = false;
        getSightings(null, offset);
    }

    @Override
    public void onRefresh() {
        if (searchMenu.isActionViewExpanded())
            searchMenu.collapseActionView();
        reset();
    }
}
