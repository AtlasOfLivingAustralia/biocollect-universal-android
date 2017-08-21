package fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.model.Species;
import au.csiro.ozatlas.model.SpeciesSearchResponse;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by sad038 on 21/8/17.
 */

public class OfflineSpeciesFragment extends BaseMainActivityFragment{
    @BindView(R.id.add_species)
    TextView addSpecies;
    @BindView(R.id.available_species)
    TextView availableSpecies;
    @BindView(R.id.clear_data)
    TextView clearData;

    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_species, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.offline_species_title));

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.showFloatingButton();

        return view;
    }

    @OnClick(R.id.add_species)
    void addSpecies(){

    }

    @OnClick(R.id.available_species)
    void availableSpecies(){

    }

    @OnClick(R.id.clear_data)
    void clearData(){
        AtlasDialogManager.alertBoxForSetting(getContext(), getString(R.string.clear_data_confirmation), getString(R.string.clear_data), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Species.class);
                    }
                });
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        sendAnalyticsScreenName("Offline Species", TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
