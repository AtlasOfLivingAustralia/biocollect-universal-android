package fragments.offline_species;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.FooterViewHolders;
import au.csiro.ozatlas.base.BaseRecyclerWithFooterViewAdapter;
import au.csiro.ozatlas.fragments.BaseListWithRefreshFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import model.SpeciesGroup;
import model.SpeciesGroupList;
import rest.SpeciesListApiService;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the  Species Groups and Animals
 * GET exploreGroups from biocache
 */
public class SpeciesGroupFragment extends BaseListWithRefreshFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<SpeciesGroup> speciesGroups = new ArrayList<>();

    /**
     * onClick listener for the recyclerview group item
     */
    View.OnClickListener onGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Bundle bundle = getArguments();
            bundle.putString(getString(R.string.group_parameter), speciesGroups.get(position).dataResourceUid);
            Fragment fragment = new SpeciesListFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        }
    };
    private SpeciesListApiService speciesListApiService;
    private int totalCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        //species group service
        speciesListApiService = new NetworkClient(getString(R.string.species_list_url)).getRetrofit().create(SpeciesListApiService.class);

        mainActivityFragmentListener.hideFloatingButton();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);


        total.setText(getString(R.string.total_group_count, 0));
        adapter = new SpeciesGroupAdapter();
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);


        recyclerView.setAdapter(adapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);


        fetchGroups(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Species Group List", TAG);
    }

    protected void fetchGroups(final int offset) {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(speciesListApiService.getGroupList(MAX, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SpeciesGroupList>() {
                    @Override
                    public void onNext(SpeciesGroupList value) {
                        totalCount = value.listCount;
                        if (offset == 0)
                            speciesGroups.clear();
                        speciesGroups.addAll(value.lists);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                        handleError(e, 0, "");
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);

                        total.setText(getString(R.string.total_group_count, totalCount));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }


    @Override
    protected void fetchItems(int offset) {
        fetchGroups(offset);
    }

    @Override
    public void onRefresh() {
        fetchGroups(0);
    }


    /**
     * View Holders for Explore Groups
     */
    private class SpeciesGroupViewHolders extends RecyclerView.ViewHolder {
        TextView name, count, date;

        SpeciesGroupViewHolders(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            count = (TextView) itemView.findViewById(R.id.count);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    private class SpeciesGroupAdapter extends BaseRecyclerWithFooterViewAdapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == NORMAL) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_group_list, null);
                layoutView.setOnClickListener(onGroupClickListener);
                return new SpeciesGroupViewHolders(layoutView);
            } else {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, null);
                layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new FooterViewHolders(layoutView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SpeciesGroupViewHolders) {
                SpeciesGroupViewHolders speciesGroupViewHolders = (SpeciesGroupViewHolders) holder;
                SpeciesGroup speciesGroup = speciesGroups.get(position);
                speciesGroupViewHolders.name.setText(speciesGroup.listName);
                speciesGroupViewHolders.count.setText(getString(R.string.species_count, speciesGroup.itemCount));
                speciesGroupViewHolders.date.setText(getString(R.string.last_update_date, AtlasDateTimeUtils.getFormattedDayTime(speciesGroup.lastUpdated, "dd MMM, yyyy")));
            }
        }

        /**
         * @return an extra item if the needFooter (for showing the footer) is enabled
         */
        @Override
        public int getItemCount() {
            if (needFooter)
                return speciesGroups.size() + 1; // adding footer count
            else
                return speciesGroups.size();
        }

        /**
         * if the position is equal to the sight list size then this is Footer
         * as usually the last position is sights.size()-1
         *
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            if (position == speciesGroups.size())
                return FOOTER;
            else
                return NORMAL;

        }
    }

}
