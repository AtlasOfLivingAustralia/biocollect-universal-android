package au.csiro.ozatlas.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseSettingsFragment extends BaseMainActivityFragment {

    @BindView(R.id.logout_settings_small_text)
    TextView logoutSmallText;
    @BindView(R.id.logout_settings_button)
    View logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        hideFloatingButton();

        logoutSmallText.setText(getLoggedInShortMessage());

        configureLogoutButton(logoutButton);

        return view;
    }

    protected void configureLogoutButton(View logoutButton) {
        logoutButton.setOnClickListener(logoutView ->
                AtlasDialogManager.alertBox(
                        getActivity(),
                        getString(R.string.logout_message),
                        getString(R.string.logout_title),
                        getString(R.string.logout_title),
                        (dialog, which) -> launchLoginActivity()
                )
        );
    }

    private String getLoggedInShortMessage() {
        return getString(R.string.logged_in_message, sharedPreferences.getUsername());
    }


}
