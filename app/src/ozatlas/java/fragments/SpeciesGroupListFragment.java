package fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.model.SpeciesSearchResponse;
import au.csiro.ozatlas.rest.BieApiService;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.rest.SearchSpeciesSerializer;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import model.ExploreGroup;
import rest.BioCacheApiService;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the  Species Groups
 * GET exploreGroups from biocache
 */
public class SpeciesGroupListFragment extends BaseMainActivityFragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "SpeciesGroupFragment";

    private final String FQ = "geospatial_kosher%3Atrue";
    private final String FACET = "species_group";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<ExploreGroup> exploreGroups = new ArrayList<>();
    private double latitude, longitude, radius;
    private SpeciesGroupAdapter adapter;
    private BioCacheApiService bioCacheApiService;

    /**
     * onClick listener for the recyclerview item
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            //startWebViewActivity(getString(R.string.sighting_detail_url, exploreGroups.get(position).activityId), getString(R.string.sight_detail), false);
        }
    };
    private int totalGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        //species group service
        bioCacheApiService = new NetworkClient(getString(R.string.bio_cache_url)).getRetrofit().create(BioCacheApiService.class);

        mainActivityFragmentListener.hideFloatingButton();
        setTitle(getString(R.string.species_group_title));

        //for my sighting
        Bundle bundle = getArguments();
        if (bundle != null) {
            latitude = bundle.getDouble(getString(R.string.latitude_parameter));
            longitude = bundle.getDouble(getString(R.string.longitude_parameter));
            radius = bundle.getDouble(getString(R.string.radius_parameter));
        }

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SpeciesGroupAdapter();
        recyclerView.setAdapter(adapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the groups
        fetchGroups(latitude, longitude, radius);

        return view;
    }


    protected void fetchGroups(double latitude, double longitude, double radius) {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(bioCacheApiService.getSpeciesGroupFromMap(FQ, FACET, latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<ExploreGroup>>() {
                    @Override
                    public void onNext(List<ExploreGroup> value) {
                        totalGroups = value.size();
                        exploreGroups.clear();
                        exploreGroups.addAll(value);
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
                        total.setText(getString(R.string.total_group_count, totalGroups));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    @Override
    public void onRefresh() {
        //get the groups
        fetchGroups(latitude, longitude, radius);
    }

    public class SpeciesGroupAdapter extends RecyclerView.Adapter<SpeciesGroupViewHolders> {

        @Override
        public SpeciesGroupViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_group_species, null);
            layoutView.setOnClickListener(onClickListener);
            return new SpeciesGroupViewHolders(layoutView);
        }

        @Override
        public void onBindViewHolder(SpeciesGroupViewHolders holder, int position) {
            ExploreGroup group = exploreGroups.get(position);
            holder.name.setText(group.name);
            holder.count.setText(getString(R.string.species_count, group.speciesCount));
        }

        @Override
        public int getItemCount() {
            return exploreGroups.size();
        }
    }

    /**
     * View Holders for DraftSights
     */
    class SpeciesGroupViewHolders extends RecyclerView.ViewHolder {
        TextView name, count;

        SpeciesGroupViewHolders(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            count = (TextView) itemView.findViewById(R.id.count);
        }
    }

}
