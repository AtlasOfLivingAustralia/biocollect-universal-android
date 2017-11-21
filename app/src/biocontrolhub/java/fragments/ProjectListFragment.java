package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import activity.SingleFragmentActivity;
import adapters.ProjectListAdapter;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.fragments.BaseListWithRefreshIncludingSearchFragment;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import model.ProjectList;
import model.Projects;

/**
 * Created by sad038 on 25/5/17.
 */

public class ProjectListFragment extends BaseListWithRefreshIncludingSearchFragment implements MoreButtonListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<Projects> projects = new ArrayList<>();
    private Boolean myProjects = false;
    /**
     * onClick listener for the recyclerview item
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            if (!projects.get(position).isExternal) {
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.project_id_parameter), projects.get(position).projectId);
                bundle.putString(getString(R.string.project_name_parameter), projects.get(position).name);
                bundle.putBoolean(getString(R.string.user_project_parameter), myProjects);
                bundle.putString(getString(R.string.myview_parameter), "project");
                bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.RECORD_LIST);
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                startWebViewActivity(projects.get(position).urlWeb, projects.get(position).name, false);
            }
        }
    };
    private int totalProjects;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        hideFloatingButton();

        //for my projects
        Bundle bundle = getArguments();
        if (bundle != null) {
            myProjects = bundle.getBoolean(getString(R.string.user_project_parameter));
            setTitle(getString(R.string.my_project_title));
        } else {
            setTitle(getString(R.string.all_project_title));
        }

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ProjectListAdapter(projects, onClickListener, this);
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
    public void onResume() {
        super.onResume();
        if (myProjects)
            sendAnalyticsScreenName("My Project List", TAG);
        else
            sendAnalyticsScreenName("All Project List", TAG);
    }

    /**
     * get the sighting GET sight
     *
     * @param searchTerm search string from search bar
     * @param offset     for the pagination
     */
    protected void fetchItems(String searchTerm, final int offset) {
        if (offset == 0)
            swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(restClient.getService().getProjects(getString(R.string.project_initiator), MAX, offset, true, null, searchTerm, myProjects)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ProjectList>() {
                    @Override
                    public void onNext(ProjectList value) {
                        if (value != null && value.total != null) {
                            totalProjects = value.total;
                            if (offset == 0)
                                projects.clear();
                            if (projects.size() == value.total) {
                                hasNext = false;
                            } else {
                                projects.addAll(value.projects);
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
                        total.setText(getString(R.string.total_projects, totalProjects));
                        Log.d(TAG, "onComplete");
                    }
                }));
    }

    @Override
    public void onMoreButtonClick(View view, int position) {
        startWebViewActivity(getString(R.string.project_info_url, projects.get(position).projectId), projects.get(position).name, false);
    }
}

