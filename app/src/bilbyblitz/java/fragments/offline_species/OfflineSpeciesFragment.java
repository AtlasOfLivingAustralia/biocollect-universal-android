package fragments.offline_species;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragments.setting.SettingFragmentView;

/**
 * Created by sad038 on 19/9/17.
 */

public class OfflineSpeciesFragment extends BaseMainActivityFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_species, container, false);
        setTitle(getString(R.string.offline_setting));
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.availableSpeciesLayout)
    void availableSpeciesLayout(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.searchAndAddLayout)
    void searchAndAddLayout(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.SEARCH_ADD_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.downloadFromMapLayout)
    void downloadFromMapLayout(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.MAP_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.groupSpeciesLayout)
    void groupSpeciesLayout(){

    }
}
