package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import model.ExploreAnimal;
import model.ExploreGroup;
import rest.BioCacheApiService;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the  Species Groups
 * GET exploreGroups from biocache
 */
public class ExploreSpeciesListFragment extends BaseMainActivityFragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "SpeciesGroupFragment";

    private final String FQ = "geospatial_kosher:true";
    private final String FACET = "species_group";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<ExploreGroup> exploreGroups = new ArrayList<>();
    private List<ExploreAnimal> exploreAnimals = new ArrayList<>();
    private double latitude, longitude, radius;
    private String group;
    private RecyclerView.Adapter adapter;
    private BioCacheApiService bioCacheApiService;
    private boolean isForAnimals;

    /**
     * onClick listener for the recyclerview item
     */
    View.OnClickListener onAnimalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.species_parameter), exploreAnimals.get(position));
            Fragment fragment = new SpeciesDetailFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        }
    };

    View.OnClickListener onGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Bundle bundle = getArguments();
            bundle.putBoolean(getString(R.string.is_for_animal_parameter), true);
            bundle.putString(getString(R.string.group_parameter), exploreGroups.get(position).name);
            Fragment fragment = new ExploreSpeciesListFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        }
    };

    private int totalCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        //species group service
        bioCacheApiService = new NetworkClient(getString(R.string.bio_cache_url)).getRetrofit().create(BioCacheApiService.class);

        mainActivityFragmentListener.hideFloatingButton();

        Bundle bundle = getArguments();
        if (bundle != null) {
            isForAnimals = bundle.getBoolean(getString(R.string.is_for_animal_parameter));
            group = bundle.getString(getString(R.string.group_parameter));
            latitude = bundle.getDouble(getString(R.string.latitude_parameter));
            longitude = bundle.getDouble(getString(R.string.longitude_parameter));
            radius = bundle.getDouble(getString(R.string.radius_parameter));
            if(isForAnimals){
                setTitle(getString(R.string.species_animal_title));
            }else{
                setTitle(getString(R.string.species_group_title));
            }
        }

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(isForAnimals)
            adapter = new SpeciesAnimalAdapter();
        else
            adapter = new SpeciesGroupAdapter();

        recyclerView.setAdapter(adapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the groups
        if(isForAnimals)
            fetchAnimals(group, latitude, longitude, radius);
        else
            fetchGroups(latitude, longitude, radius);

        return view;
    }


    protected void fetchGroups(double latitude, double longitude, double radius) {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(bioCacheApiService.getSpeciesGroupFromMap(FQ, FACET, 27.76, 138.55, 532.0)//latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<ExploreGroup>>() {
                    @Override
                    public void onNext(List<ExploreGroup> value) {
                        totalCount = value.size();
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
                        total.setText(getString(R.string.total_group_count, totalCount));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    protected void fetchAnimals(String group, double latitude, double longitude, double radius) {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(bioCacheApiService.getSpeciesAnimalFromMap(group, FQ, FACET, 27.76, 138.55, 532.0)//latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<ExploreAnimal>>() {
                    @Override
                    public void onNext(List<ExploreAnimal> value) {
                        totalCount = value.size();
                        exploreAnimals.clear();
                        exploreAnimals.addAll(value);
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
                        total.setText(getString(R.string.total_species_count, totalCount));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    @Override
    public void onRefresh() {
        if(isForAnimals)
            fetchAnimals(group, latitude, longitude, radius);
        else
            fetchGroups(latitude, longitude, radius);
    }

    private class SpeciesGroupAdapter extends RecyclerView.Adapter<SpeciesGroupViewHolders> {

        @Override
        public SpeciesGroupViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_group_species, null);
            layoutView.setOnClickListener(onGroupClickListener);
            return new SpeciesGroupViewHolders(layoutView);
        }

        @Override
        public void onBindViewHolder(SpeciesGroupViewHolders holder, int position) {
            ExploreGroup group = exploreGroups.get(position);
            holder.name.setText(group.name);
            holder.count.setText(getString(R.string.count, group.speciesCount));
        }

        @Override
        public int getItemCount() {
            return exploreGroups.size();
        }
    }

    /**
     * View Holders for Explore Groups
     */
    class SpeciesGroupViewHolders extends RecyclerView.ViewHolder {
        TextView name, count;

        SpeciesGroupViewHolders(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            count = (TextView) itemView.findViewById(R.id.count);
        }
    }

    private class SpeciesAnimalAdapter extends RecyclerView.Adapter<SpeciesAnimalViewHolders> {

        @Override
        public SpeciesAnimalViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_animal, null);
            layoutView.setOnClickListener(onAnimalClickListener);
            return new SpeciesAnimalViewHolders(layoutView);
        }

        @Override
        public void onBindViewHolder(SpeciesAnimalViewHolders holder, int position) {
            ExploreAnimal animal = exploreAnimals.get(position);
            holder.name.setText(animal.name);
            holder.count.setText(getString(R.string.count, animal.count));
            holder.family.setText(animal.family);
        }

        @Override
        public int getItemCount() {
            return exploreAnimals.size();
        }
    }

    /**
     * View Holders for Exploring Animals
     */
    class SpeciesAnimalViewHolders extends SpeciesGroupViewHolders {
        TextView family;

        SpeciesAnimalViewHolders(View itemView) {
            super(itemView);
            family = (TextView) itemView.findViewById(R.id.family);
        }
    }
}
