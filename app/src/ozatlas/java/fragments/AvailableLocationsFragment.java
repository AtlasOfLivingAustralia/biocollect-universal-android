package fragments;

import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import model.OzAtlasLocation;

/**
 * Created by sad038 on 21/8/17.
 */

public class AvailableLocationsFragment extends BaseMainActivityFragment {
    @BindView(R.id.listView)
    ListView listView;

    private Realm realm;
    private LocationAdapter locationAdapter;
    RealmResults<OzAtlasLocation> locationsRealmResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.available_locations));
        setHasOptionsMenu(true);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        locationsRealmResults = realm.where(OzAtlasLocation.class).findAll();
        locationAdapter = new LocationAdapter();
        listView.setAdapter(locationAdapter);
        return view;
    }

    @Override
    public void onResume(){
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
     * Adapters for Species ListView
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
            holder.addressLine.setText(location.addressLine);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AtlasDialogManager.alertBoxForSetting(getContext(), getString(R.string.delete_species_confirmation), getString(R.string.delete_species_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    location.deleteFromRealm();
                                    locationAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            });

            return rowView;
        }

        @Override
        public int getCount(){
            return locationsRealmResults.size();
        }

        class ViewHolder {
            ImageView delete;
            TextView addressLine;
        }
    }
}
