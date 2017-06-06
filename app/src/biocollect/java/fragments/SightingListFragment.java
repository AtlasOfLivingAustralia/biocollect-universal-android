package fragments;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SightAdapter;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.fragments.BaseListWithRefreshFragment;
import au.csiro.ozatlas.model.Sight;
import au.csiro.ozatlas.model.SightList;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import dialog.SurveyBottomSheetDialogFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import model.Survey;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the sights
 * GET sights from biocollect
 */
public class SightingListFragment extends BaseListWithRefreshFragment implements MoreButtonListener {
    private final String TAG = "SightingListFragment";
    private final static int MAX = 20;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;


    private List<Sight> sights = new ArrayList<>();
    private ArrayList<Survey> surveys = new ArrayList<>();
    private String viewQuery;
    private int totalSighting;
    private String projectId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        hideFloatingButton();

        //for my sighting
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.viewQuery = bundle.getString(getString(R.string.myview_parameter));
            projectId = "bb227dec-f7d7-4bdf-873d-41924c102e1d"; //bundle.getString(getString(R.string.project_id_parameter));
            if (bundle.getBoolean(getString(R.string.user_project_parameter)))
                getSurveys(projectId);
        }

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new SightAdapter(sights, onClickListener, this, this.viewQuery);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting
        fetchItems(null, 0);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, R.id.add, Menu.NONE, "Add").setVisible(false).setIcon(R.drawable.ic_add_white_36dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (surveys.size() > 0)
            menu.findItem(R.id.add).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                BottomSheetDialogFragment bottomSheetDialogFragment = new SurveyBottomSheetDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.survey_list_parameter), surveys);
                bundle.putString(getString(R.string.title_parameter), getString(R.string.survey_dialog_title));
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());
                break;
        }
        return false;
    }

    /**
     * get the survey list
     * to show in the bottom sheet dialog fragment
     */
    private void getSurveys(String projectId) {
        mCompositeDisposable.add(restClient.getService().getSurveys(projectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Survey>>() {
                    @Override
                    public void onNext(List<Survey> value) {
                        surveys.addAll(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        handleError(e, 0, "");
                    }

                    @Override
                    public void onComplete() {
                        getActivity().supportInvalidateOptionsMenu();
                    }
                }));
    }

    /**
     * show popup menu from the more button of recyclerview items
     *
     * @param view
     * @param position
     */
    @Override
    public void onPopupMenuClick(View view, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sight_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //do your things in each of the following cases
                switch (item.getItemId()) {
                    case R.id.delete:

                        break;
                    case R.id.edit:

                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    /**
     * onClick listener for the recyclerview item
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            startWebViewActivity(getString(R.string.sighting_detail_url, sights.get(position).activityId), getString(R.string.sight_detail), false);
        }
    };


    /**
     * get the sighting GET sight
     *
     * @param searchTerm search string from search bar
     * @param offset     for the pagination
     */
    protected void fetchItems(String searchTerm, final int offset) {
        if (offset == 0)
            swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(restClient.getService().getSightings(projectId, MAX, offset, true, viewQuery == null ? "project" : viewQuery, searchTerm, sharedPreferences.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SightList>() {
                    @Override
                    public void onNext(SightList value) {
                        if (value != null && value.total != null) {
                            totalSighting = value.total;
                            if (offset == 0)
                                sights.clear();
                            if (sights.size() == value.total) {
                                hasNext = false;
                            } else {
                                sights.addAll(value.activities);
                            }
                            adapter.setNeedFooter(false);
                            adapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        handleError(e, 0, "");
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        total.setText(getString(R.string.total_sighting, totalSighting));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }
}
