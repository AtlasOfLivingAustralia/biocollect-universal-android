package service;

import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseIntentService;
import au.csiro.ozatlas.model.Project;
import io.reactivex.observers.DisposableObserver;
import model.ProjectList;

public class FetchProjectListService extends BaseIntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchProjectListService() {
        super("FetchAndSaveSpeciesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (sharedPreferences.getSelectedProject() == null && !sharedPreferences.getAuthKey().equals(""))
            fetchProjects();
    }

    private void fetchProjects() {
        mCompositeDisposable.add(restClient.getService().getProjects(getString(R.string.project_initiator), 10, 0, true, null, null, null)
                .subscribeWith(new DisposableObserver<ProjectList>() {
                    @Override
                    public void onNext(ProjectList value) {
                        if (value != null && value.total != null && value.projects.size() > 0) {
                            getProjectDetails(value.projects.get(0).projectId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                }));
    }


    private void getProjectDetails(String projectId) {
        mCompositeDisposable.add(restClient.getService().getProjectDetail(projectId)
                .subscribeWith(new DisposableObserver<List<Project>>() {
                    @Override
                    public void onNext(List<Project> value) {
                        if (value != null) {
                            for (Project project : value) {
                                if (project.status.equals("active")) {
                                    sharedPreferences.writeSelectedProject(project);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                }));
    }
}
