package fragments.offline_species;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SearchSpeciesAdapter;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.model.Species;
import au.csiro.ozatlas.model.SpeciesSearchResponse;
import au.csiro.ozatlas.rest.BieApiService;
import au.csiro.ozatlas.rest.NetworkClient;
import au.csiro.ozatlas.rest.SearchSpeciesSerializer;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by sad038 on 21/8/17.
 */

public class SearchAndAddFragment extends BaseMainActivityFragment {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.editSpeciesName)
    EditText editSpeciesName;

    List<SpeciesSearchResponse.Species> species = new ArrayList<>();
    private Realm realm;
    private BieApiService bieApiService;
    private static final int DELAY_IN_MILLIS = 400;
    SpeciesAdapter speciesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_add, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.available_species));
        setHasOptionsMenu(true);

        Gson gson = new GsonBuilder().registerTypeAdapter(SpeciesSearchResponse.class, new SearchSpeciesSerializer()).create();
        bieApiService = new NetworkClient(getString(R.string.bie_url), gson).getRetrofit().create(BieApiService.class);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        speciesAdapter = new SpeciesAdapter();
        listView.setAdapter(speciesAdapter);
        mCompositeDisposable.add(getSearchSpeciesResponseObserver());
        return view;
    }


    /**
     * network call for species suggestion
     *
     * @return
     */
    private DisposableObserver<SpeciesSearchResponse> getSearchSpeciesResponseObserver() {
        return RxTextView.textChangeEvents(editSpeciesName)
                .debounce(DELAY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .map(new Function<TextViewTextChangeEvent, String>() {
                    @Override
                    public String apply(TextViewTextChangeEvent textViewTextChangeEvent) throws Exception {
                        Log.d(TAG, textViewTextChangeEvent.text().toString());
                        return textViewTextChangeEvent.text().toString();
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        Log.d(TAG, s);
                        return s.length() > 1;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<SpeciesSearchResponse>>() {
                    @Override
                    public ObservableSource<SpeciesSearchResponse> apply(String s) throws Exception {
                        Log.d(TAG, s);
                        return bieApiService.searchSpecies(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribeWith(new DisposableObserver<SpeciesSearchResponse>() {
                    @Override
                    public void onNext(SpeciesSearchResponse speciesSearchResponse) {
                        Log.d(TAG, species.size()+"");
                        species.clear();
                        species.addAll(speciesSearchResponse.results);
                        speciesAdapter.notifyDataSetChanged();
                        //listView.setAdapter(new SpeciesAdapter());
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleError(e, 0, "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

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

    /**
     * Adapters for Species ListView
     */
    private class SpeciesAdapter extends ArrayAdapter<String> {

        SpeciesAdapter() {
            super(getActivity(), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.row_item_online_species, parent, false);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.addButton = (Button) rowView.findViewById(R.id.addButton);
                viewHolder.speciesName = (TextView) rowView.findViewById(R.id.species_name);
                viewHolder.kingdomName = (TextView) rowView.findViewById(R.id.kingdom_name);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            final SpeciesSearchResponse.Species species = SearchAndAddFragment.this.species.get(position);
            holder.speciesName.setText(species.name);
            if(species.kingdom==null){
                holder.kingdomName.setVisibility(View.GONE);
            }else {
                holder.kingdomName.setVisibility(View.VISIBLE);
                holder.kingdomName.setText(getString(R.string.kingdom_name, species.kingdom));
            }
            holder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return rowView;
        }

        @Override
        public int getCount(){
            return species.size();
        }

        class ViewHolder {
            Button addButton;
            TextView speciesName;
            TextView kingdomName;
        }
    }
}