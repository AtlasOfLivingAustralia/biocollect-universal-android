package fragments.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        listView.setOnItemClickListener((parent, view1, position, id) -> {
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
        name.setText(getString(R.string.good_day_message, sharedPreferences.getUserDisplayName()));
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
        item.url = getString(R.string.partners_url);
        item.text = getString(R.string.partners);
        item.textResource = R.string.partners;
        item.languageKey = "partners";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_help_outline_black_24dp;
        item.text = getString(R.string.help);
        item.url = getString(R.string.help_url);
        item.textResource = R.string.help;
        item.languageKey = "help";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_icon_transparent;
        item.text = getString(R.string.about);
        item.textResource = R.string.about;
        item.url = getString(R.string.about_url);
        item.languageKey = "about";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_mail_outline_black_24dp;
        item.text = getString(R.string.contact);
        item.url = getString(R.string.contact_us_url);
        item.textResource = R.string.contact;
        item.languageKey = "contact";
        item.isForWebView = true;
        listItems.add(item);
        item = new BilbyHomePageListItem();
        item.icon = R.drawable.ic_info_outline_black_24dp;
        item.text = getString(R.string.biocollect);
        item.url = getString(R.string.biocollect_url);
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
