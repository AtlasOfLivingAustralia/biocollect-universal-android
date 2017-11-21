package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.model.HomePageListItem;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 1/9/17.
 */

public class HomePageFragment extends BaseMainActivityFragment {

    @BindView(R.id.listView)
    ListView listView;

    //list of items in home page
    private List<HomePageListItem> listItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        setTitle(getString(R.string.app_name));
        ButterKnife.bind(this, view);

        //preparing the header
        View header = getActivity().getLayoutInflater().inflate(R.layout.header_list, null);
        setupHeader(header);
        listView.addHeaderView(header);

        //preparign the list
        prepareItemList();
        listView.setAdapter(new HomePageItemAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //subtracting 1 due to adding header
                if (position > 0) {
                    HomePageListItem item = listItems.get(position - 1);
                    if (item.text.equals(getString(R.string.add_title))) {
                        setDrawerMenuChecked(R.id.nav_add);
                        setDrawerMenuClicked(R.id.nav_add);
                    } else if (item.text.equals(getString(R.string.my_sighting_title))) {
                        setDrawerMenuChecked(R.id.nav_my_sighting);
                        setDrawerMenuClicked(R.id.nav_my_sighting);
                    } else if (item.text.equals(getString(R.string.draft_sighting_title))) {
                        setDrawerMenuChecked(R.id.nav_draft_sighting);
                        setDrawerMenuClicked(R.id.nav_draft_sighting);
                    } else if (item.text.equals(getString(R.string.all_sighting_title))) {
                        setDrawerMenuChecked(R.id.nav_all_sighting);
                        setDrawerMenuClicked(R.id.nav_all_sighting);
                    } else if (item.text.equals(getString(R.string.map_explore_title))) {
                        setDrawerMenuChecked(R.id.nav_location_species);
                        setDrawerMenuClicked(R.id.nav_location_species);
                    } else if (item.text.equals(getString(R.string.about_title))) {
                        //setDrawerMenuChecked(R.id.);
                        setDrawerMenuClicked(R.id.nav_about);
                    } else if (item.text.equals(getString(R.string.contact_us_title))) {
                        //setDrawerMenuChecked(R.id.);
                        setDrawerMenuClicked(R.id.nav_contact);
                    }
                }
            }
        });
        return view;
    }

    /**
     * setting up the header information of the option list
     *
     * @param header
     */
    private void setupHeader(View header) {
        TextView name = (TextView) header.findViewById(R.id.name);
        TextView logout = (TextView) header.findViewById(R.id.logoutButton);
        name.setText(getString(R.string.good_day_message, sharedPreferences.getUserDisplayName()));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginActivity();
            }
        });
    }

    /**
     * preparing the items for the list at home page
     */
    private void prepareItemList() {
        HomePageListItem item = new HomePageListItem();
        item.icon = R.drawable.ic_add_to_photos_black_24dp;
        item.text = getString(R.string.add_title);
        item.isForWebView = false;
        listItems.add(item);
        item = new HomePageListItem();
        item.icon = R.drawable.ic_collections_black_24dp;
        item.text = getString(R.string.my_sighting_title);
        item.isForWebView = false;
        listItems.add(item);
        item = new HomePageListItem();
        item.icon = R.drawable.ic_collections_black_24dp;
        item.text = getString(R.string.draft_sighting_title);
        item.isForWebView = false;
        listItems.add(item);
        item = new HomePageListItem();
        item.icon = R.drawable.ic_collections_black_24dp;
        item.text = getString(R.string.all_sighting_title);
        item.isForWebView = false;
        listItems.add(item);
        item = new HomePageListItem();
        item.icon = R.drawable.ic_location_on_black_24dp;
        item.text = getString(R.string.map_explore_title);
        item.isForWebView = false;
        listItems.add(item);
        item = new HomePageListItem();
        item.icon = R.drawable.ic_info_outline_black_24dp;
        item.text = getString(R.string.about_title);
        item.isForWebView = true;
        listItems.add(item);
        item = new HomePageListItem();
        item.icon = R.drawable.ic_mail_outline_black_24dp;
        item.text = getString(R.string.contact_us_title);
        item.isForWebView = true;
        listItems.add(item);
    }

    /**
     * sedting the analytics info
     */
    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Home Page", TAG);
    }


    /**
     * Home Page List Adapter
     */
    private class HomePageItemAdapter extends ArrayAdapter<HomePageListItem> {

        HomePageItemAdapter() {
            super(getActivity(), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.row_item_home_page, parent, false);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) rowView.findViewById(R.id.imageView);
                viewHolder.textView = (TextView) rowView.findViewById(R.id.text_view);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            HomePageListItem item = listItems.get(position);
            holder.imageView.setImageResource(item.icon);
            holder.textView.setText(item.text);

            return rowView;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

}
