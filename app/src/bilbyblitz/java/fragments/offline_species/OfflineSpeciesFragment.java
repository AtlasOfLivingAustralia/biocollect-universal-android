package fragments.offline_species;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sad038 on 19/9/17.
 */

public class OfflineSpeciesFragment extends BaseMainActivityFragment {

    @BindView(R.id.projectSpinner)
    Spinner projectSpinner;
    @BindView(R.id.languageSpinner)
    Spinner languageSpinner;


    private String[] projects = new String[]{};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_species, container, false);
        setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        languageSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.bilby_language, R.layout.item_textview));
        projectSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.bilby_project, R.layout.item_textview));
        return view;
    }

    @OnClick(R.id.availableSpeciesLayout)
    void availableSpeciesLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.searchAndAddLayout)
    void searchAndAddLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.SEARCH_ADD_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.downloadFromMapLayout)
    void downloadFromMapLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.MAP_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.groupSpeciesLayout)
    void groupSpeciesLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.GROUP_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
