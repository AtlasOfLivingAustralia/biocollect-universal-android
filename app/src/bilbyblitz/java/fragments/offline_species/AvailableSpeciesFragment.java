package fragments.offline_species;

import android.content.DialogInterface;
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
import au.csiro.ozatlas.model.Species;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sad038 on 21/8/17.
 */

public class AvailableSpeciesFragment extends BaseMainActivityFragment {
    @BindView(R.id.listView)
    ListView listView;
    RealmResults<Species> speciesRealmResults;
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_list, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.available_species));
        setHasOptionsMenu(true);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        speciesRealmResults = realm.where(Species.class).findAll();
        listView.setAdapter(new SpeciesAdapter());
        return view;
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
                rowView = inflater.inflate(R.layout.row_item_species, parent, false);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.delete = (ImageView) rowView.findViewById(R.id.delete);
                viewHolder.speciesName = (TextView) rowView.findViewById(R.id.species_name);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            final Species species = speciesRealmResults.get(position);
            holder.speciesName.setText(species.name);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AtlasDialogManager.alertBoxForSetting(getContext(), getString(R.string.delete_species_confirmation), getString(R.string.delete_species_title), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            species.deleteFromRealm();
                        }
                    });
                }
            });

            return rowView;
        }

        class ViewHolder {
            ImageView delete;
            TextView speciesName;
        }
    }
}
