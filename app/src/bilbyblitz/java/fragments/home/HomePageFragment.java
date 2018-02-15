package fragments.home;

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
import au.csiro.ozatlas.manager.Language;
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
    private List<BilbyHomePageListItem> listItems = new ArrayList<>();
    private ArrayAdapter itemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        setTitle(getString(R.string.app_name));
        ButterKnife.bind(this, view);

        hideFloatingButton();

        //preparing the header
        View header = getActivity().getLayoutInflater().inflate(R.layout.header_list, null);
        setupHeader(header);
        listView.addHeaderView(header);

        //preparign the list
        prepareItemList();
        itemAdapter = new HomePageItemAdapter();
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //subtracting 1 due to adding header
                if (position > 0) {
                    HomePageListItem item = listItems.get(position - 1);
                    if (item.isForWebView) {
                        startWebViewActivity(item.url, item.text, false);
                    } else if (item.text.equals(getString(R.string.add_track))) {
                        setDrawerMenuChecked(R.id.nav_add_track);
                        setDrawerMenuClicked(R.id.nav_add_track);
                    } else if (item.text.equals(getString(R.string.practise_track))) {
                        setDrawerMenuChecked(R.id.nav_practise_track);
                        setDrawerMenuClicked(R.id.nav_practise_track);
                    } else if (item.text.equals(getString(R.string.review_track))) {
                        setDrawerMenuChecked(R.id.nav_review_track);
                        setDrawerMenuClicked(R.id.nav_review_track);
                    } else if (item.text.equals(getString(R.string.my_track))) {
                        setDrawerMenuChecked(R.id.nav_my_track);
                        setDrawerMenuClicked(R.id.nav_my_track);
                    } else if (item.text.equals(getString(R.string.setting))) {
                        setDrawerMenuChecked(R.id.nav_setting);
                        setDrawerMenuClicked(R.id.nav_setting);
                    } else if (item.text.equals(getString(R.string.help))) {
                        setDrawerMenuClicked(R.id.nav_help);
                    } else if (item.text.equals(getString(R.string.contact))) {
                        setDrawerMenuClicked(R.id.nav_contact);
                    } else if (item.text.equals(getString(R.string.about))) {
                        setDrawerMenuClicked(R.id.nav_about);
                    } else if (item.text.equals(getString(R.string.partners))) {
                        setDrawerMenuClicked(R.id.nav_partners);
                    }
                }
            }
        });

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

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
                setDrawerMenuClicked(R.id.nav_logout);
            }
        });
    }

    /**
     * preparing the items for the list at home page
     */
    private void prepareItemList() {
        BilbyHomePageListItem item = new BilbyHomePageListItem();
        item.icon = R.drawable.animal_track;
        item.text = getString(R.string.add_track);
        item.textResource = R.string.add_track;
        item.languageKey = "add_track";
        item.isForWebView = false;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.animal_practise_track;
        item.text = getString(R.string.practise_track);
        item.textResource = R.string.practise_track;
        item.languageKey = "practise_track";
        item.isForWebView = false;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.review;
        item.text = getString(R.string.review_track);
        item.textResource = R.string.review_track;
        item.languageKey = "review_track";
        item.isForWebView = false;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.my_track_icon;
        item.text = getString(R.string.my_track);
        item.textResource = R.string.my_track;
        item.languageKey = "my_track";
        item.isForWebView = false;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_setting_outline;
        item.text = getString(R.string.setting);
        item.textResource = R.string.setting;
        item.languageKey = "setting";
        item.isForWebView = false;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_partners;
        item.text = getString(R.string.partners);
        item.textResource = R.string.partners;
        item.languageKey = "partners";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_help_outline_black_24dp;
        item.text = getString(R.string.help);
        item.textResource = R.string.help;
        item.languageKey = "help";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_icon_transparent;
        item.text = getString(R.string.about);
        item.textResource = R.string.about;
        item.languageKey = "about";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_mail_outline_black_24dp;
        item.text = getString(R.string.contact);
        item.textResource = R.string.contact;
        item.languageKey = "contact";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_info_outline_black_24dp;
        item.text = getString(R.string.biocollect);
        item.textResource = R.string.biocollect;
        item.languageKey = "biocollect";
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

    @Override
    protected void setLanguageValues(Language language) {
        itemAdapter.notifyDataSetChanged();
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
            BilbyHomePageListItem item = listItems.get(position);
            holder.imageView.setImageResource(item.icon);
            holder.textView.setText(localisedString(item.languageKey, item.textResource));

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

    class BilbyHomePageListItem extends HomePageListItem {
        int textResource;
        String languageKey;
    }
}
