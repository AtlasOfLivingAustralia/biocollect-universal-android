package fragments.animal;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import au.csiro.ozatlas.R;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 9/10/17.
 */

public class AddAnimalFragment extends BaseMainActivityFragment {
    @BindView(R.id.animalKindSpinner)
    AppCompatSpinner animalKindSpinner;
    @BindView(R.id.sizeSpinner)
    AppCompatSpinner sizeSpinner;
    @BindView(R.id.coveringSpinner)
    AppCompatSpinner coveringSpinner;
    @BindView(R.id.animalNameSpinner)
    AppCompatSpinner whatSeenSpinner;
    @BindView(R.id.whatSeenSpinner)
    AppCompatSpinner animalNameSpinner;
    @BindView(R.id.howRecentSpinner)
    AppCompatSpinner howRecentSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animal, container, false);
        //setTitle(getString(R.string.setting));
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

