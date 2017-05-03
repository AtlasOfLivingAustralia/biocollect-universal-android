package au.csiro.ozatlas.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.fragments.AddSightingFragment;
import au.csiro.ozatlas.fragments.WebViewFragment;

/**
 * Created by sad038 on 21/4/17.
 */

public class SingleFragmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        Fragment fragment = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FragmentType fragmentType = (FragmentType) bundle.getSerializable(getString(R.string.fragment_type_parameter));
            setTitle(bundle.getString(getString(R.string.title_parameter), getString(R.string.title_activity_main)), true);
            switch (fragmentType){
                case WEB_FRAGMENT:
                    fragment = new WebViewFragment();
                    break;
                case EDIT_FRAGMENT:
                    fragment = new AddSightingFragment();
                    break;
            }
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle(String str, boolean homeButton) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(homeButton);
            getSupportActionBar().setTitle(str);
        } else {
            setTitle(str);
        }
    }

    public enum FragmentType {
        WEB_FRAGMENT,
        EDIT_FRAGMENT
    }
}
