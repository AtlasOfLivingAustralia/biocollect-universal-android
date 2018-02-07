package fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SightAdapter;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.fragments.BaseListWithRefreshIncludingSearchFragment;
import au.csiro.ozatlas.manager.Language;
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

/**
 * This class is to show the tracks
 */
public class TrackListFragment extends BaseListWithRefreshIncludingSearchFragment implements MoreButtonListener {
    private final static int MAX = 20;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;


    private List<Sight> sights = new ArrayList<>();
    /**
     * onClick listener for the recyclerview item
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            startWebViewActivity(getString(R.string.sighting_detail_url, sights.get(position).activityId), getString(R.string.sight_detail), false);
        }
    };
    private int totalSighting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        hideFloatingButton();
        setTitle(getString(R.string.my_track));

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new SightAdapter(sights, onClickListener, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting
        fetchItems(null, 0);

        return view;
    }


    /**
     * show popup menu from the more button of recyclerview items
     *
     * @param view
     * @param position
     */
    @Override
    public void onMoreButtonClick(View view, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sight_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //do your things in each of the following cases
                switch (item.getItemId()) {
                    case R.id.delete:
                        //// TODO: 21/6/17  
                        break;
                    case R.id.edit:
                        startWebViewActivity(getString(R.string.sighting_edit_url, sights.get(position).activityId), getString(R.string.edit_title), true);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * get the sighting GET sight
     *
     * @param searchTerm search string from search bar
     * @param offset     for the pagination
     */
    protected void fetchItems(String searchTerm, final int offset) {
        if (offset == 0)
            swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(restClient.getService().getSightings(null, MAX, offset, true, "myrecords", searchTerm, sharedPreferences.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SightList>() {
                    @Override
                    public void onNext(SightList value) {
                        if (value != null && value.total != null) {
                            totalSighting = value.total;
                            if (offset == 0)
                                sights.clear();
                            if (sights.size() == value.total) {
                                hasNext = false;
                            } else {
                                sights.addAll(value.activities);
                            }
                            adapter.setNeedFooter(false);
                            adapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        handleError(e, 0, "");
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
    protected void setLanguageValues(Language language) {

    }
}
