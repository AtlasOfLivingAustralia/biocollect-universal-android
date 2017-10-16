package fragments.offline_species;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.FooterViewHolders;
import au.csiro.ozatlas.base.BaseRecyclerWithFooterViewAdapter;
import au.csiro.ozatlas.fragments.BaseListWithRefreshFragment;
import au.csiro.ozatlas.model.ExploreAnimal;
import au.csiro.ozatlas.model.ExploreGroup;
import au.csiro.ozatlas.rest.BioCacheApiService;
import au.csiro.ozatlas.rest.NetworkClient;
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
 * This class is to show the  Species Groups and Animals
 * GET exploreGroups from biocache
 */
public class ExploreSpeciesListFragment extends BaseListWithRefreshFragment {
    private final String FQ = "geospatial_kosher:true";
    private final String FACET = "species_group";
    private final int[] icons = {R.drawable.algae, R.drawable.amphibians, R.drawable.angiosperms,
            R.drawable.birds, R.drawable.bryophytes, R.drawable.crustaceans, R.drawable.dicots,
            R.drawable.fernsandallies, R.drawable.fish,
            R.drawable.fungi, R.drawable.gymnosperms, R.drawable.insects,
            R.drawable.mammals, R.drawable.molluscs, R.drawable.monocots,
            R.drawable.plants, R.drawable.reptiles};
    private final String[] groups = new String[]{"Algae", "Amphibians", "Angiosperms", "Birds", "Bryophytes", "Crustaceans",
            "Dicots", "FernsAndAllies", "Fishes", "Fungi", "Gymnosperms", "Insects", "Mammals", "Molluscs", "Monocots",
            "Plants", "Reptiles"};
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;
    private List<ExploreGroup> exploreGroups = new ArrayList<>();
    /**
     * onClick listener for the recyclerview group item
     */
    View.OnClickListener onGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Bundle bundle = getArguments();
            bundle.putBoolean(getString(R.string.is_for_animal_parameter), true);
            bundle.putSerializable(getString(R.string.group_parameter), exploreGroups.get(position));
            Fragment fragment = new ExploreSpeciesListFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        }
    };
    private List<ExploreAnimal> exploreAnimals = new ArrayList<>();
    private double latitude, longitude, radius;
    /**
     * onClick listener for the recyclerview animal item
     */
    /*View.OnClickListener onAnimalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.species_parameter), exploreAnimals.get(position));
            bundle.putDouble(getString(R.string.latitude_parameter), latitude);
            bundle.putDouble(getString(R.string.longitude_parameter), longitude);
            Fragment fragment = new SpeciesDetailFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, fragment).addToBackStack(null).commit();
        }
    };*/
    private ExploreGroup group;
    private BioCacheApiService bioCacheApiService;
    private boolean isForAnimals;
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
            group = (ExploreGroup) bundle.getSerializable(getString(R.string.group_parameter));
            latitude = bundle.getDouble(getString(R.string.latitude_parameter));
            longitude = bundle.getDouble(getString(R.string.longitude_parameter));
            radius = bundle.getDouble(getString(R.string.radius_parameter));
            /*if (isForAnimals) {
                setTitle(getString(R.string.species_animal_title));
            } else {
                setTitle(getString(R.string.species_group_title));
            }*/
        }

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);

        if (isForAnimals) {
            adapter = new SpeciesAnimalAdapter();
            recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        } else
            adapter = new SpeciesGroupAdapter();

        recyclerView.setAdapter(adapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the groups
        if (isForAnimals)
            fetchAnimals(group.name, /*27.76, 138.55, 532.0);//(group,*/ latitude, longitude, radius, 0);
        else
            fetchGroups(/*27.76, 138.55, 532.0);//*/latitude, longitude, radius);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isForAnimals)
            sendAnalyticsScreenName("Explore Animal List", TAG);
        else
            sendAnalyticsScreenName("Explore Group List", TAG);
    }

    protected void fetchGroups(double latitude, double longitude, double radius) {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(bioCacheApiService.getSpeciesGroupFromMap(FQ, FACET, latitude, longitude, radius) //27.76, 138.55, 532.0)//
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

                        //total.setText(getString(R.string.total_group_count, totalCount));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    protected void fetchAnimals(String groupName, double latitude, double longitude, double radius, final int offset) {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(bioCacheApiService.getSpeciesAnimalFromMap(groupName, FQ, FACET, latitude, longitude, radius, MAX, offset) //27.76, 138.55, 532.0)//
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<ExploreAnimal>>() {
                    @Override
                    public void onNext(List<ExploreAnimal> value) {
                        if (value != null) {
                            if (offset == 0)
                                exploreAnimals.clear();

                            exploreAnimals.addAll(value);
                            if (group.speciesCount == exploreAnimals.size())
                                hasNext = false;
                            totalCount = exploreAnimals.size();
                            adapter.setNeedFooter(false);
                            adapter.notifyDataSetChanged();
                        }
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
                        //total.setText(getString(R.string.total_species_count, totalCount));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    @Override
    protected void fetchItems(int offset) {
        fetchAnimals(group.name, latitude, longitude, radius, offset);
    }

    @Override
    public void onRefresh() {
        if (isForAnimals)
            fetchAnimals(group.name, /*27.76, 138.55, 532.0);//(group,*/ latitude, longitude, radius, 0);
        else
            fetchGroups(/*27.76, 138.55, 532.0);//*/latitude, longitude, radius);
    }

    private class SpeciesGroupAdapter extends BaseRecyclerWithFooterViewAdapter {
        Map<String, Integer> map = new HashMap<>();

        SpeciesGroupAdapter() {
            for (int i = 0; i < groups.length; i++) {
                map.put(groups[i], icons[i]);
            }
        }

        @Override
        public SpeciesGroupViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_group_species, null);
            layoutView.setOnClickListener(onGroupClickListener);
            return new SpeciesGroupViewHolders(layoutView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SpeciesGroupViewHolders) {
                SpeciesGroupViewHolders speciesGroupViewHolders = (SpeciesGroupViewHolders) holder;
                ExploreGroup group = exploreGroups.get(position);
                speciesGroupViewHolders.name.setText(group.name);
                speciesGroupViewHolders.count.setText(getString(R.string.species_count, group.speciesCount));

                Integer icon = map.get(group.name);
                if (icon != null) {
                    speciesGroupViewHolders.icon.setImageResource(icon);
                } else {
                    speciesGroupViewHolders.icon.setImageResource(R.drawable.no_image_available);
                }
            }
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
        ImageView icon;

        SpeciesGroupViewHolders(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            count = (TextView) itemView.findViewById(R.id.count);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

    private class SpeciesAnimalAdapter extends BaseRecyclerWithFooterViewAdapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == NORMAL) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_animal, null);
                //layoutView.setOnClickListener(onAnimalClickListener);
                return new SpeciesAnimalViewHolders(layoutView);
            } else {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, null);
                layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new FooterViewHolders(layoutView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SpeciesAnimalViewHolders) {
                SpeciesAnimalViewHolders speciesAnimalViewHolders = (SpeciesAnimalViewHolders) holder;
                ExploreAnimal animal = exploreAnimals.get(position);
                speciesAnimalViewHolders.name.setText(animal.name);
                speciesAnimalViewHolders.count.setText(getString(R.string.species_count, animal.count));
                speciesAnimalViewHolders.family.setText(animal.commonName == null ? animal.family : animal.commonName);
                Glide.with(getActivity())
                        .load(getString(R.string.explore_image_url, animal.guid))
                        .placeholder(R.drawable.no_image_available)
                        .crossFade()
                        .into(speciesAnimalViewHolders.icon);
            }
        }

        /**
         * @return an extra item if the needFooter (for showing the footer) is enabled
         */
        @Override
        public int getItemCount() {
            if (needFooter)
                return exploreAnimals.size() + 1; // adding footer count
            else
                return exploreAnimals.size();
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
            if (position == exploreAnimals.size())
                return FOOTER;
            else
                return NORMAL;

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
