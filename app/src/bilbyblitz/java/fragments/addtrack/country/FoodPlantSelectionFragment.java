package fragments.addtrack.country;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.Language;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 22/6/17.
 */

/**
 * This class is to show all the available foodPlants and to select multiple foodPlants
 */
public class FoodPlantSelectionFragment extends BaseMainActivityFragment {
    @BindView(R.id.listView)
    ListView listView;

    private ArrayAdapter<String> foodPlantAdapter;
    private List<String> foodPlants;
    private boolean[] selections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_plant_list, container, false);
        ButterKnife.bind(this, view);
        setTitle(getString(R.string.food_plant));
        setHasOptionsMenu(true);

        //click the item of the lsitview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selections[position] = !selections[position];
                foodPlantAdapter.notifyDataSetChanged();
            }
        });
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());
        selections = new boolean[foodPlants.size()];

        String[] existingTags = null;
        if (getArguments() != null) {
            String str = getArguments().getString(getString(R.string.food_plant_parameter));
            existingTags = TextUtils.split(str, getString(R.string.food_plant_separator));
            if (existingTags != null)
                for (int i = 0; i < foodPlants.size(); i++) {
                    for (String existingTag : existingTags)
                        if (foodPlants.get(i).equals(existingTag))
                            selections[i] = true;
                }
        }
        foodPlantAdapter = new FoodPlantAdapter();
        listView.setAdapter(foodPlantAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when the user will press the donr menu item
            case R.id.done:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(getString(R.string.food_plant_parameter), getFoodPlants());
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
                break;
        }
        return true;
    }

    /**
     * joining the selected foodPlants separated by getString(R.string.tag_separator)
     *
     * @return
     */
    private String getFoodPlants() {
        List<String> selectedTags = new ArrayList<>();
        for (int i = 0; i < foodPlants.size(); i++) {
            if (selections[i]) {
                selectedTags.add(foodPlants.get(i));
            }
        }
        return TextUtils.join(getString(R.string.food_plant_separator), selectedTags);
    }

    @Override
    protected void setLanguageValues(Language language) {
        switch (language) {
            case WARLPIRI:
                foodPlants = Arrays.asList(getResources().getStringArray(R.array.food_plant_type_adithinngithigh));
                break;
            case WARUMUNGU:
                foodPlants = Arrays.asList(getResources().getStringArray(R.array.food_plant_type_warumungu));
                break;
            default:
                foodPlants = Arrays.asList(getResources().getStringArray(R.array.food_plant_type));
                break;
        }
    }

    /**
     * Adapters for Tag ListView
     */
    private class FoodPlantAdapter extends ArrayAdapter<String> {

        FoodPlantAdapter() {
            super(getActivity(), 0, foodPlants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.row_item_tag, parent, false);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.tick = (ImageView) rowView.findViewById(R.id.tick);
                viewHolder.tagName = (TextView) rowView.findViewById(R.id.tag_name);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.tagName.setText(foodPlants.get(position));
            if (selections != null) {
                if (selections[position]) {
                    holder.tick.setBackgroundResource(R.drawable.filled_circle);
                    holder.tick.setImageResource(R.drawable.ic_done_white_24dp);
                } else {
                    holder.tick.setBackgroundResource(R.drawable.ring);
                    holder.tick.setImageResource(0);
                }
            }
            return rowView;
        }

        class ViewHolder {
            ImageView tick;
            TextView tagName;
        }
    }

}
