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
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 1/9/17.
 */

public class HomePageFragment extends BaseMainActivityFragment {

    @BindView(R.id.listView)
    ListView listView;

    private List<ListItem> listItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        setTitle(getString(R.string.app_name));
        ButterKnife.bind(this, view);

        View header = getActivity().getLayoutInflater().inflate(R.layout.header_list, null);
        setupHeader(header);
        listView.addHeaderView(header);

        prepareItemList();
        listView.setAdapter(new HomePageItemAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //subtracting 1 due to adding header
                ListItem item = listItems.get(position - 1);
                if (item.isForWebView) {
                    startWebViewActivity(item.url, item.text, false);
                } else if (item.text.equals(getString(R.string.all_project_title))) {
                    setDrawerMenuClicked(R.id.nav_all_projects);
                } else if (item.text.equals(getString(R.string.all_record_title))) {
                    setDrawerMenuClicked(R.id.nav_all_sighting);
                }
            }
        });
        return view;
    }

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

    private void prepareItemList() {
        ListItem item = new ListItem();
        item.icon = R.drawable.ic_card_travel_black_24dp;
        item.text = getString(R.string.all_project_title);
        item.isForWebView = false;
        listItems.add(item);
        item = new ListItem();
        item.icon = R.drawable.ic_folder_open_black_24dp;
        item.text = getString(R.string.all_record_title);
        item.isForWebView = false;
        listItems.add(item);
        item = new ListItem();
        item.icon = R.drawable.ic_info_outline_black_24dp;
        item.text = getString(R.string.case_study_title);
        item.isForWebView = true;
        item.url = UrlConstants.CASE_STUDY_URL;
        listItems.add(item);
        item = new ListItem();
        item.icon = R.drawable.ic_info_outline_black_24dp;
        item.text = getString(R.string.additional_resources_title);
        item.url = UrlConstants.ADDITIONAL_RESOURCES_URL;
        item.isForWebView = true;
        listItems.add(item);
        item = new ListItem();
        item.icon = R.drawable.ic_info_outline_black_24dp;
        item.text = getString(R.string.get_involced_title);
        item.url = UrlConstants.GET_INVOLVED_URL;
        item.isForWebView = true;
        listItems.add(item);
        item = new ListItem();
        item.icon = R.drawable.ic_info_outline_black_24dp;
        item.text = getString(R.string.about_title);
        item.url = UrlConstants.ABOUT_URL;
        item.isForWebView = true;
        listItems.add(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Home Page", TAG);
    }

    private class ListItem {
        String text;
        int icon;
        String url;
        boolean isForWebView;
    }

    public interface UrlConstants {
        String BASE_URL = "https://biocollect.ala.org.au/";
        String CASE_STUDY_URL = BASE_URL + "/biocontrolhub/staticPage/index?page=related_websites&mobile=true";
        String ADDITIONAL_RESOURCES_URL = BASE_URL + "/biocontrolhub/staticPage/index?page=materialss&mobile=true";
        String GET_INVOLVED_URL = BASE_URL + "/biocontrolhub/staticPage/index?page=get_involved";
        String ABOUT_URL = BASE_URL + "/biocontrolhub/staticPage/index?page=what_is_biocontrol&mobile=true";
    }

    private class HomePageItemAdapter extends ArrayAdapter<ListItem> {

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
            ListItem item = listItems.get(position);
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
