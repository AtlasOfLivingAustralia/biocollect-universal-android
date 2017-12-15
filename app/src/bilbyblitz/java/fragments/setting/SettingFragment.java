package fragments.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.Utils;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import language.LanguageManager;
import model.Project;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 19/9/17.
 */

public class SettingFragment extends BaseMainActivityFragment {

    private final int PROJECT_LIST_REQUEST_CODE = 1;
    @BindView(R.id.languageSpinner)
    Spinner languageSpinner;
    @BindView(R.id.projectDescription)
    TextView projectDescription;
    @BindView(R.id.offlineHeading)
    TextView offlineHeading;
    @BindView(R.id.availableSpecies)
    TextView availableSpecies;
    @BindView(R.id.availableSpeciesDescription)
    TextView availableSpeciesDescription;
    @BindView(R.id.searchAndAdd)
    TextView searchAndAdd;
    @BindView(R.id.searchAndAddDescription)
    TextView searchAndAddDescription;
    @BindView(R.id.downloadFromMap)
    TextView downloadFromMap;
    @BindView(R.id.downloadFromMapDescription)
    TextView downloadFromMapDescription;
    @BindView(R.id.groupSpecies)
    TextView groupSpecies;
    @BindView(R.id.groupSpeciesDescription)
    TextView groupSpeciesDescription;
    @BindView(R.id.projectHeading)
    TextView projectHeading;
    @BindView(R.id.languageHeading)
    TextView languageHeading;
    //Language Selection Listener from language spinner
    AdapterView.OnItemSelectedListener onLanguageSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                sharedPreferences.writeSelectedLanguage(null);
                sharedPreferences.writeSelectedLanguageFileName(null);
                LanguageManager.languageJSON = null;
                EventBus.getDefault().post("");
            } else {
                sharedPreferences.writeSelectedLanguage((String) languageSpinner.getItemAtPosition(position));
                sharedPreferences.writeSelectedLanguageFileName(languageSpinner.getItemAtPosition(position) + ".json");
                loadLanguageFile(languageSpinner.getItemAtPosition(position) + ".json");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        hideFloatingButton();

        //language selection spinner setup
        String[] languages = getResources().getStringArray(R.array.bilby_language);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.bilby_language, R.layout.item_textview);
        languageSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.dropdown_item_textview);
        int position = Utils.stringSearchInArray(languages, sharedPreferences.getLanguage());
        languageSpinner.setSelection(position == -1 ? 0 : position, false);
        languageSpinner.setOnItemSelectedListener(onLanguageSelectedListener);

        //set the localized labels
        setLanguageValues();

        return view;
    }

    /**
     * To show the available offline species
     */
    @OnClick(R.id.availableSpeciesLayout)
    void availableSpeciesLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.AVAILABLE_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * the user will be able to search the species and add one by one
     * using a button with each item
     */
    @OnClick(R.id.searchAndAddLayout)
    void searchAndAddLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.SEARCH_ADD_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * User can download species from map
     */
    @OnClick(R.id.downloadFromMapLayout)
    void downloadFromMapLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.MAP_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * User will select a project from available projects from server
     */
    @OnClick(R.id.projectLayout)
    void projectLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.PROJECT_SELECTION);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, PROJECT_LIST_REQUEST_CODE);
    }

    /**
     * There are species groups in the server
     * and the user will be able to see the group name with the number
     * of species in each group. The user will be able to see all the species and they will be able to download a group
     * of species
     */
    @OnClick(R.id.groupSpeciesLayout)
    void groupSpeciesLayout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.GROUP_SPECIES);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Set the project name if there is already one saved in sharedpreference
     */
    private void setProjectName() {
        Project project = sharedPreferences.getSelectedProject();
        if (project != null)
            projectDescription.setText(project.name);
        else
            projectDescription.setText(R.string.no_selected_project);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROJECT_LIST_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setProjectName();
            }
        }
    }

    /**
     * set language values
     */
    @Override
    protected void setLanguageValues() {
        setTitle(localisedString("setting", R.string.setting));
        offlineHeading.setText(localisedString("offline_species", R.string.offline_species));
        availableSpecies.setText(localisedString("available_species", R.string.available_species));
        availableSpeciesDescription.setText(localisedString("available_species_description", R.string.available_species_description));
        searchAndAdd.setText(localisedString("search_and_add", R.string.search_and_add));
        searchAndAddDescription.setText(localisedString("search_and_add_description", R.string.search_and_add_description));
        downloadFromMap.setText(localisedString("download_from_map", R.string.download_from_map));
        downloadFromMapDescription.setText(localisedString("download_from_map_description", R.string.download_from_map_description));
        groupSpecies.setText(localisedString("group_species", R.string.group_species));
        groupSpeciesDescription.setText(localisedString("group_species_description", R.string.group_species_description));
        projectHeading.setText(localisedString("select_project", R.string.select_project));
        projectDescription.setText(localisedString("no_selected_project", R.string.no_selected_project));
        languageHeading.setText(localisedString("choose_language", R.string.choose_language));
        setProjectName();
    }
}
