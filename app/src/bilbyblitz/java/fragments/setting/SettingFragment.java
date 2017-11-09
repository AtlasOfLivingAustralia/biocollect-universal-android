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

    @BindView(R.id.languageSpinner)
    Spinner languageSpinner;
    @BindView(R.id.projectDescription)
    TextView projectDescription;

    private final int PROJECT_LIST_REQUEST_CODE = 1;

    //Language Selection Listener from language spinner
    AdapterView.OnItemSelectedListener onLanguageSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                sharedPreferences.writeSelectedLanguage(null);
                sharedPreferences.writeSelectedLanguageFileName(null);
                LanguageManager.languageJSON = null;
            } else {
                sharedPreferences.writeSelectedLanguage((String) languageSpinner.getItemAtPosition(position));
                sharedPreferences.writeSelectedLanguageFileName(languageSpinner.getItemAtPosition(position) + ".json");
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

        //language selection spinner setup
        String[] languages = getResources().getStringArray(R.array.bilby_language);
        languageSpinner.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.bilby_language, R.layout.item_textview));
        int position = Utils.stringSearchInArray(languages, sharedPreferences.getLanguage());
        languageSpinner.setSelection(position == -1 ? 0 : position, false);
        languageSpinner.setOnItemSelectedListener(onLanguageSelectedListener);

        //get the project name from sharedpreference (if available) and show the value
        setProjectName();

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

    @Override
    protected void setLanguageValues() {

    }
}
