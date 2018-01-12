package fragments.setting;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseRecyclerWithFooterViewAdapter;
import au.csiro.ozatlas.fragments.BaseListWithRefreshFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.manager.Language;
import au.csiro.ozatlas.model.Project;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import model.ProjectList;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the  Species Groups and Animals
 * GET exploreGroups from biocache
 */
public class ProjectListFragment extends BaseListWithRefreshFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private List<Project> projects = new ArrayList<>();
    private int selectedPosition = -1;
    /**
     * onClick listener for the recyclerview group item
     */
    View.OnClickListener onProjectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int oldPosition = selectedPosition;
            selectedPosition = recyclerView.getChildAdapterPosition(v);
            adapter.notifyItemChanged(oldPosition);
            adapter.notifyItemChanged(selectedPosition);
        }
    };
    private int totalCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        mainActivityFragmentListener.hideFloatingButton();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);

        total.setText(getString(R.string.total_species_count, 0));
        adapter = new SpeciesAdapter();

        recyclerView.setAdapter(adapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        fetchProjects();

        //set the localized labels
        setLanguageValues(sharedPreferences.getLanguageEnumLanguage());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.select, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when the user will press the done menu item
            case R.id.select:
                getActivity().setResult(Activity.RESULT_OK);
                if (selectedPosition != -1)
                    sharedPreferences.writeSelectedProject(projects.get(selectedPosition));
                getActivity().finish();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Project List", TAG);
    }

    /**
     * getting the projects
     */

    protected void fetchProjects() {
        swipeRefreshLayout.setRefreshing(true);
        mCompositeDisposable.add(restClient.getService().getProjects(getString(R.string.project_initiator), 50, 0, true, null, null, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ProjectList>() {
                    @Override
                    public void onNext(ProjectList value) {
                        if (value != null && value.total != null) {
                            totalCount = value.total;
                            ProjectListFragment.this.projects.clear();
                            ProjectListFragment.this.projects.addAll(value.projects);
                            Project previousSelectedProject = sharedPreferences.getSelectedProject();
                            if (previousSelectedProject != null) {
                                for (int i = 0; i < projects.size(); i++) {
                                    if (projects.get(i).projectId.equals(previousSelectedProject.projectId)) {
                                        selectedPosition = i;
                                        break;
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
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
                        Log.d(TAG, "onComplete");
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        total.setText(getString(R.string.species_count, totalCount));
                    }
                }));
    }

    @Override
    protected void fetchItems(int offset) {

    }

    @Override
    public void onRefresh() {
        fetchProjects();
    }

    @Override
    protected void setLanguageValues(Language language) {

    }

    /**
     * View Holders for Project List
     */
    class ProjectViewHolders extends RecyclerView.ViewHolder {
        TextView name, user, time, type;
        ImageView image, infoButton;

        ProjectViewHolders(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            user = (TextView) itemView.findViewById(R.id.user);
            time = (TextView) itemView.findViewById(R.id.time);
            type = (TextView) itemView.findViewById(R.id.type);
            image = (ImageView) itemView.findViewById(R.id.image);
            infoButton = (ImageView) itemView.findViewById(R.id.infoIcon);
        }
    }

    private class SpeciesAdapter extends BaseRecyclerWithFooterViewAdapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, null);
            layoutView.setOnClickListener(onProjectClickListener);
            return new ProjectViewHolders(layoutView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ProjectViewHolders) {
                final ProjectViewHolders projectViewHolders = (ProjectViewHolders) holder;
                final Project project = ProjectListFragment.this.projects.get(position);
                projectViewHolders.name.setText(project.name);
                projectViewHolders.type.setText(project.organisationName);
                projectViewHolders.user.setText(project.projectType);
                projectViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(project.startDate, "dd MMM, yyyy"));
                Glide.with(projectViewHolders.image.getContext())
                        .load(project.urlImage)
                        .placeholder(R.drawable.no_image_available)
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                projectViewHolders.image.setColorFilter(Color.WHITE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                projectViewHolders.image.clearColorFilter();
                                return false;
                            }
                        })
                        .into(projectViewHolders.image);
                if (position == selectedPosition) {
                    projectViewHolders.infoButton.setImageResource(R.drawable.ic_done_white_24dp);
                    projectViewHolders.infoButton.setBackgroundResource(R.drawable.filled_circle);
                } else {
                    projectViewHolders.infoButton.setBackgroundResource(R.drawable.ring);
                    projectViewHolders.infoButton.setImageResource(0);
                }
            }
        }

        @Override
        public int getItemCount() {
            return ProjectListFragment.this.projects.size();
        }
    }
}
