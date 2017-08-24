package fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import model.OzAtlasLocation;

/**
 * Created by sad038 on 21/8/17.
 */

/**
 * Showing the offline locations
 * for deleting as well as for selecting
 */
public class AvailableLocationsFragment extends BaseMainActivityFragment {
    @BindView(R.id.listView)
    ListView listView;

    private Realm realm;
    private LocationAdapter locationAdapter;
    private RealmResults<OzAtlasLocation> locationsRealmResults;
    private boolean isForLocationSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.available_locations));
        setHasOptionsMenu(true);

        //retieving bundle information
        Bundle bundle = getArguments();
        if (bundle != null) {
            isForLocationSelection = bundle.getBoolean(getString(R.string.is_location_selection_parameter), false);
        }

        locationAdapter = new LocationAdapter();

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        locationsRealmResults = realm.where(OzAtlasLocation.class).findAllAsync();
        locationsRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<OzAtlasLocation>>() {
            @Override
            public void onChange(RealmResults<OzAtlasLocation> collection, OrderedCollectionChangeSet changeSet) {
                listView.setAdapter(locationAdapter);
            }
        });

        //if the request is for selecting an offline location
        //then add the item click listener and finisg the activity when the user clicks on a location row
        if (isForLocationSelection) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.location_selection_parameter), locationsRealmResults.get(position).id);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().onBackPressed();
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Available Location List", TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }

    /**
     * Adapters for Locations ListView
     */
    private class LocationAdapter extends ArrayAdapter<String> {

        LocationAdapter() {
            super(getActivity(), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.row_item_species, parent, false);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.delete = (ImageView) rowView.findViewById(R.id.delete);
                viewHolder.addressLine = (TextView) rowView.findViewById(R.id.species_name);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            final OzAtlasLocation location = locationsRealmResults.get(position);
            if (location.addressLine == null || location.addressLine.equals(""))
                holder.addressLine.setText(String.format(Locale.getDefault(), "%.4f, %.4f", location.latitude, location.longitude));
            else
                holder.addressLine.setText(location.addressLine);

            if (isForLocationSelection) {
                holder.delete.setVisibility(View.INVISIBLE);
            } else {
                //clicking on the cross button in each row
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AtlasDialogManager.alertBoxForSetting(getContext(), getString(R.string.delete_location_confirmation), getString(R.string.delete_species_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        //deleting the location item from Realm database
                                        location.deleteFromRealm();
                                        locationAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                });
            }

            return rowView;
        }

        @Override
        public int getCount() {
            if (locationsRealmResults != null)
                return locationsRealmResults.size();
            else
                return 0;
        }

        class ViewHolder {
            ImageView delete;
            TextView addressLine;
        }
    }
}
