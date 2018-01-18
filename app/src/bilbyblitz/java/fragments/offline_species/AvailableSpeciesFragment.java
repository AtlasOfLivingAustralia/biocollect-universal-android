package fragments.offline_species;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.Language;
import au.csiro.ozatlas.model.KvpValues;
import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import model.EventBusPosts;

/**
 * Created by sad038 on 21/8/17.
 */

public class AvailableSpeciesFragment extends BaseMainActivityFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<SearchSpecies> filterSpecies = new ArrayList<>();
    private List<SearchSpecies> species = new ArrayList<>();
    private SpeciesAdapter speciesAdapter;
    private Realm realm;
    private boolean[] selections;
    private int selectedPosition = -1;

    /**
     * onClick listener for the recyclerview group item
     */
    View.OnClickListener onAnimalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectedPosition = recyclerView.getChildAdapterPosition(v);
            Arrays.fill(selections, false);
            selections[selectedPosition] = true;
            speciesAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.available_species));

        if (mainActivityFragmentListener != null) {
            mainActivityFragmentListener.hideFloatingButton();
        }

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        speciesAdapter = new SpeciesAdapter();
        recyclerView.setAdapter(speciesAdapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the filterSpecies
        readAvailableSpecies();

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.species_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusPosts eventBusPosts) {
        if (eventBusPosts.equals(EventBusPosts.FETCH_SPECIES_LIST)) {
            readAvailableSpecies();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select:
                if (selectedPosition != -1) {
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.species_parameter), filterSpecies.get(selectedPosition).realmId);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().onBackPressed();
                }
                break;
            case R.id.filter:
                SpeciesFilterBottomSheetDialogFragment bottomSheetDialogFragment = new SpeciesFilterBottomSheetDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.species_filter_parameter), Parcels.wrap(sharedPreferences.getSpeciesFilter()));
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.setBottomSheetListener(speciesFilter -> {
                    sharedPreferences.writeSpeciesFilter(speciesFilter);
                    applyFilter(speciesFilter);
                });
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                break;
        }
        return true;
    }

    private void applyFilter(SpeciesFilterBottomSheetDialogFragment.SpeciesFilter speciesFilter) {
        filterSpecies.clear();
        if (speciesFilter != null) {
            for (int i = 0; i < species.size(); i++) {
                SearchSpecies spc = species.get(i);
                if (spc.kvpValues != null) {
                    for (KvpValues kvpValues : spc.kvpValues) {
                        if (kvpValues.key.equals("Adult  Size")) {
                            Log.d("SPECIES", kvpValues.key+ "    "+kvpValues.value);
                            if (speciesFilter.isSizeSmall && kvpValues.value.equals("Small")) {
                                filterSpecies.add(spc);
                                break;
                            }

                            if (speciesFilter.isSizeMedium && kvpValues.value.equals("Medium")) {
                                filterSpecies.add(spc);
                                break;
                            }

                            if (speciesFilter.isSizeLarge && kvpValues.value.equals("Large")) {
                                filterSpecies.add(spc);
                                break;
                            }
                        } else if (kvpValues.key.equals("Body  Cover")) {
                            if (speciesFilter.isBodyCoverFur && kvpValues.value.equals("Fur")) {
                                filterSpecies.add(spc);
                                break;
                            }

                            if (speciesFilter.isBodyCoverFeather && kvpValues.value.equals("Feathers")) {
                                filterSpecies.add(spc);
                                break;
                            }
                        }
                    }
                }
            }
        }else{
            filterSpecies.addAll(species);
        }
        selections = new boolean[filterSpecies.size()];
        speciesAdapter.notifyDataSetChanged();
        selectedPosition = -1;
        updateTotal();
    }

    private void updateTotal() {
        total.setText(getString(R.string.total_species, filterSpecies.size()));
    }

    public void readAvailableSpecies() {
        RealmResults<SearchSpecies> results = realm.where(SearchSpecies.class).findAllAsync();
        results.addChangeListener((collection, changeSet) -> {
            if (isAdded()) {
                species.clear();
                species.addAll(collection);
                applyFilter(sharedPreferences.getSpeciesFilter());
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        readAvailableSpecies();
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Available Species List", TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }

    @Override
    protected void setLanguageValues(Language language) {
    }

    /**
     * Adapters for Species ListView
     */
    private class SpeciesAdapter extends RecyclerView.Adapter<SpeciesAdapter.SpeciesViewHolder> {

        @Override
        public int getItemCount() {
            return filterSpecies.size();
        }

        @Override
        public SpeciesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_species, null);
            layoutView.setOnClickListener(onAnimalClickListener);
            return new SpeciesViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(final SpeciesAdapter.SpeciesViewHolder holder, final int position) {
            final SearchSpecies species = AvailableSpeciesFragment.this.filterSpecies.get(position);
            holder.speciesName.setText(species.name);
            holder.commonName.setText(getString(R.string.common_name, species.commonName));
            if (species.kingdom == null) {
                if (species.scientificName != null) {
                    holder.kingdomName.setText(getString(R.string.scientific_name, species.scientificName));
                } else {
                    holder.kingdomName.setText(getString(R.string.kingdom_name, "Undefined"));
                }
            } else {
                holder.kingdomName.setText(getString(R.string.kingdom_name, species.kingdom));
            }
            if (selections != null) {
                if (selections[position]) {
                    holder.delete.setBackgroundResource(R.drawable.filled_circle);
                    holder.delete.setImageResource(R.drawable.ic_done_white_24dp);
                } else {
                    holder.delete.setBackgroundResource(R.drawable.ring);
                    holder.delete.setImageResource(0);
                }
            }

            /*if (species.kvpValues != null) {
                for (KvpValues kvpValues : species.kvpValues) {
                    if (kvpValues.key.equals("Image")) {
                        Glide.with(getActivity())
                                .load(kvpValues.value)
                                .placeholder(R.drawable.no_image_available)
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        holder.image.setColorFilter(Color.WHITE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        holder.image.clearColorFilter();
                                        return false;
                                    }
                                })
                                .into(holder.image);
                    }
                }
            }*/
            /*if (species.kvpValues != null) {
                for (KvpValues kvpValues : species.kvpValues) {
                    if (kvpValues.key.equals("Adult  Size")) {
                        Log.d("SPECIES", kvpValues.key+ "    "+kvpValues.value);
                        holder.kingdomName.setText(kvpValues.value);
                    }
                    if (kvpValues.key.equals("Body  Cover")) {
                        holder.kingdomName.append("   " + kvpValues.value);
                    }
                }
            }*/
        }

        class SpeciesViewHolder extends RecyclerView.ViewHolder {
            ImageView delete, image;
            TextView speciesName, kingdomName, commonName;

            public SpeciesViewHolder(View itemView) {
                super(itemView);
                speciesName = (TextView) itemView.findViewById(R.id.species_name);
                kingdomName = (TextView) itemView.findViewById(R.id.kingdom_name);
                commonName = (TextView) itemView.findViewById(R.id.common_name);
                delete = (ImageView) itemView.findViewById(R.id.delete);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }
}
