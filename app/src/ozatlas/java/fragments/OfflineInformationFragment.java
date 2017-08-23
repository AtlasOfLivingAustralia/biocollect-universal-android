package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.model.Species;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by sad038 on 21/8/17.
 */

public class OfflineInformationFragment extends BaseMainActivityFragment{
    @BindView(R.id.add_species)
    TextView addSpecies;
    @BindView(R.id.available_species)
    TextView availableSpecies;
    @BindView(R.id.clear_species)
    TextView clearSpecies;
    @BindView(R.id.add_location)
    TextView addLocation;
    @BindView(R.id.available_location)
    TextView availableLocations;
    @BindView(R.id.clear_location)
    TextView clearLocations;

    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_information, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.offline_information_title));

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.hideFloatingButton();

        return view;
    }

    @OnClick(R.id.add_species)
    void addSpecies(){

    }

    @OnClick(R.id.available_species)
    void availableSpecies(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_SPECIES_FRAGMENT);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.clear_species)
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

    @OnClick(R.id.add_location)
    void addLocation(){

    }

    @OnClick(R.id.available_location)
    void availableLocations(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_SPECIES_FRAGMENT);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.clear_location)
    void clearLocation(){
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
        sendAnalyticsScreenName("Offline Information", TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
