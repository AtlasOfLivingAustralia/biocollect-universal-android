package fragments.offline_species;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseRecyclerWithFooterViewAdapter;
import au.csiro.ozatlas.fragments.BaseListWithRefreshFragment;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import rest.SpeciesListApiService;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the  Species Groups and Animals
 * GET exploreGroups from biocache
 */
public class SpeciesListFragment extends BaseListWithRefreshFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<SearchSpecies> species = new ArrayList<>();
    private String dataResourceId;
    private boolean[] addButtonFlag;
    private Realm realm;
    private boolean isSaved;

    private SpeciesListApiService speciesListApiService;
    private int totalCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        realm = Realm.getDefaultInstance();

        //species group service
        speciesListApiService = new NetworkClient(getString(R.string.species_list_url)).getRetrofit().create(SpeciesListApiService.class);

        mainActivityFragmentListener.hideFloatingButton();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);


        dataResourceId = getArguments().getString(getString(R.string.group_parameter));

        total.setText(getString(R.string.total_species_count, 0));
        adapter = new SpeciesAdapter();


        recyclerView.setAdapter(adapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        fetchSpecies();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isSaved)
            inflater.inflate(R.menu.add_all, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when the user will press the upload menu item
            case R.id.add_all:
                AtlasDialogManager.alertBoxForSetting(getActivity(), getString(R.string.add_all_species_confirmation), getString(R.string.add_all_species_title), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Arrays.fill(addButtonFlag, true);
                        adapter.notifyDataSetChanged();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    Log.d(TAG + "COUNT", "count" + SpeciesListFragment.this.species.size());
                                    for (SearchSpecies sp : species) {
                                        sp.realmId = sp.guid + sp.id;
                                        Log.d(TAG + "ID", sp.guid + "   " + sp.id + "   " + sp.realmId);
                                        realm.copyToRealm(sp);
                                    }
                                } catch (RealmPrimaryKeyConstraintException exception) {

                                }
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                if (isAdded()) {
                                    isSaved = false;
                                    getActivity().invalidateOptionsMenu();
                                    showSnackBarMessage(getString(R.string.download_service_complete));
                                }
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                if (isAdded()) {
                                    showSnackBarMessage(error.getMessage());
                                }
                            }
                        });
                    }
                });

                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Species List", TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }

    protected void fetchSpecies() {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(speciesListApiService.getSpeciesList(dataResourceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<SearchSpecies>>() {
                    @Override
                    public void onNext(List<SearchSpecies> value) {
                        totalCount = value.size();
                        SpeciesListFragment.this.species.clear();
                        SpeciesListFragment.this.species.addAll(value);
                        addButtonFlag = new boolean[SpeciesListFragment.this.species.size()];
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

                        total.setText(getString(R.string.species_count, totalCount));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    @Override
    protected void fetchItems(int offset) {

    }

    @Override
    public void onRefresh() {
        fetchSpecies();
    }

    private void saveData(final SearchSpecies species) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    species.realmId = species.guid + species.id;
                    Log.d(TAG + "ID", species.guid + "   " + species.id + "   " + species.realmId);
                    realm.copyToRealm(species);
                } catch (RealmPrimaryKeyConstraintException exception) {
                    showSnackBarMessage(getString(R.string.duplicate_entry));
                }
            }
        });
    }

    @Override
    protected void setLanguageValues() {

    }


    /**
     * View Holders for Explore Groups
     */
    private class SpeciesViewHolders extends RecyclerView.ViewHolder {
        TextView name, kingdom;
        Button addButton;

        SpeciesViewHolders(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.species_name);
            kingdom = (TextView) itemView.findViewById(R.id.kingdom_name);
            addButton = (Button) itemView.findViewById(R.id.addButton);
        }
    }

    private class SpeciesAdapter extends BaseRecyclerWithFooterViewAdapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_online_species, null);
            return new SpeciesViewHolders(layoutView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SpeciesViewHolders) {
                SpeciesViewHolders speciesViewHolders = (SpeciesViewHolders) holder;
                final SearchSpecies searchSpecies = SpeciesListFragment.this.species.get(position);
                Log.d(TAG, searchSpecies.id);
                speciesViewHolders.name.setText(searchSpecies.name);
                speciesViewHolders.kingdom.setText(searchSpecies.scientificName);
                if (addButtonFlag[position]) {
                    speciesViewHolders.addButton.setVisibility(View.INVISIBLE);
                    speciesViewHolders.addButton.setOnClickListener(null);
                } else {
                    speciesViewHolders.addButton.setAlpha(1.0f);
                    speciesViewHolders.addButton.setVisibility(View.VISIBLE);
                    speciesViewHolders.addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addButtonFlag[holder.getAdapterPosition()] = true;
                            v.animate().alpha(0.0f).setListener(animationListener).start();
                            saveData(searchSpecies);
                        }
                    });
                }
            }
        }

        private AnimatorListenerAdapter animationListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notifyDataSetChanged();
            }
        };

        @Override
        public int getItemCount() {
            return SpeciesListFragment.this.species.size();
        }
    }
}
