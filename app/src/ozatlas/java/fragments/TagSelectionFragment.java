package fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.FileUtils;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sad038 on 22/6/17.
 */

/**
 * This class is to show all the available tags and to select multiple tags
 */
public class TagSelectionFragment extends BaseMainActivityFragment {
    @BindView(R.id.listView)
    ListView listView;

    private ArrayAdapter<String> tagsAdapter;
    private List<String> tags = new ArrayList<>();
    private boolean[] selections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.identification_tags));
        setHasOptionsMenu(true);

        //click the item of the lsitview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selections[position] = !selections[position];
                tagsAdapter.notifyDataSetChanged();
            }
        });

        //reading the tags from file
        showProgressDialog();
        mCompositeDisposable.add(getFileReadObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        Log.d("", value);
                        createTagLists(value);
                        selections = new boolean[tags.size()];

                        String[] existingTags = null;

                        if (getArguments() != null) {
                            String str = getArguments().getString(getString(R.string.tag_string_parameter));
                            existingTags = TextUtils.split(str, getString(R.string.tag_separator));
                            if (existingTags != null)
                                for (int i = 0; i < tags.size(); i++) {
                                    for (String existingTag : existingTags)
                                        if (tags.get(i).equals(existingTag))
                                            selections[i] = true;
                                }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnackBarMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                        tagsAdapter = new TagAdapter();
                        listView.setAdapter(tagsAdapter);
                    }
                }));
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        sendAnalyticsScreenName("Individual Tag List");
    }

    /**
     * Observable to read the tag.txt file
     *
     * @return
     */
    private Observable<String> getFileReadObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(FileUtils.readAsset("tags.txt", getActivity()));
            }
        });
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
                resultIntent.putExtra(getString(R.string.tag_string_parameter), getTags());
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
                break;
        }
        return true;
    }

    /**
     * joining the selected tags separated by getString(R.string.tag_separator)
     * @return
     */
    private String getTags() {
        List<String> selectedTags = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            if (selections[i]) {
                selectedTags.add(tags.get(i));
            }
        }
        return TextUtils.join(getString(R.string.tag_separator), selectedTags);
    }

    /**
     * @param json to make the string list for keys
     * @return
     */
    private List<String> createTagLists(String json) {
        Set<String> set = new HashSet<>();

        try {
            JSONObject jObject = new JSONObject(json);
            Iterator<?> keys = jObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                if (!set.contains(value)) {
                    tags.add(value);
                    set.add(value);
                }
                tags.add(value.concat(" - ").concat(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tags;
    }

    /**
     * Adapters for Tag ListView
     */
    private class TagAdapter extends ArrayAdapter<String> {

        TagAdapter() {
            super(getActivity(), 0, tags);
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
            holder.tagName.setText(tags.get(position));
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
