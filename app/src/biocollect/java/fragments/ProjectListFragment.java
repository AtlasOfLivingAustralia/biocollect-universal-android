package fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.fragments.BaseListWithRefreshFragment;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.Project;

/**
 * Created by sad038 on 25/5/17.
 */

public class ProjectListFragment extends BaseListWithRefreshFragment {
    private final String TAG = "ProjectListFragment";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<Project> projects = new ArrayList<>();
    private Boolean myProjects;
    private int totalSighting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        mainActivityFragmentListener.showFloatingButton();

        //for my sighting
        Bundle bundle = getArguments();
        if (bundle != null) {
            myProjects = bundle.getBoolean(getString(R.string.user_project_parameter));
        }

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLayoutManager(mLayoutManager);
        //adapter = new ProjectListAdapter(projects, onClickListener, this, myProjects);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting
        fetchItems(null, 0);

        return view;
    }




    /**
     * onClick listener for the recyclerview item
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            //startWebViewActivity(getString(R.string.sighting_detail_url, projects.get(position).activityId), getString(R.string.sight_detail));
        }
    };



    /**
     * get the sighting GET sight
     * @param searchTerm search string from search bar
     * @param offset for the pagination
     */
    protected void fetchItems(String searchTerm, final int offset) {
        /*if (offset == 0)
            swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(restClient.getService().getSightings(getString(R.string.project_id), MAX, offset, true, myProjects, searchTerm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SightList>() {
                    @Override
                    public void onNext(SightList value) {
                        if (value != null && value.total != null) {
                            totalSighting = value.total;
                            if (offset == 0)
                                projects.clear();
                            if (projects.size() == value.total) {
                                hasNext = false;
                            } else {
                                projects.addAll(value.activities);
                            }
                            projectListAdapter.setNeedFooter(false);
                            projectListAdapter.notifyDataSetChanged();
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
                }));*/
    }
}

