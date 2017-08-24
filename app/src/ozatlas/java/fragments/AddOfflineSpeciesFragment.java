package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by sad038 on 21/8/17.
 */

public class AddOfflineSpeciesFragment extends BaseMainActivityFragment {
    @BindView(R.id.listView)
    ListView listView;

    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.add_offline_species));
        setHasOptionsMenu(true);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Add Offline Species", TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }

}
