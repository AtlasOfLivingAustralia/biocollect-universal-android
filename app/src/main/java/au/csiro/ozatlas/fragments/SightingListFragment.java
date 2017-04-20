package au.csiro.ozatlas.fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SightAdapter;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.model.Sight;
import au.csiro.ozatlas.model.SightList;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sad038 on 13/4/17.
 */

public class SightingListFragment extends BaseFragment {
    private final String TAG = "SightingListFragment";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private SightAdapter sightAdapter;
    private List<Sight> sights = new ArrayList<>();
    private String myRecords;
    private MenuItem searchMenu;
    private String searchTerm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sight_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            myRecords = bundle.getString(getString(R.string.myview_parameter));
        }

        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.grid_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        sightAdapter = new SightAdapter(sights);
        recyclerView.setAdapter(sightAdapter);

        getSightings();

        return view;
    }

    private void getSightings() {
        showProgressDialog();
        mCompositeDisposable.add(restClient.getService().getSightings(getString(R.string.project_id), 10, 0, true, myRecords, searchTerm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SightList>() {
                    @Override
                    public void onNext(SightList value) {
                        sights.clear();
                        sights.addAll(value.activities);
                        sightAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        showSnackBarMessage(coordinatorLayout, e.getMessage());
                        hideProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                        Log.d(TAG, "onComplete");
                    }
                }));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        searchMenu = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTerm = query;
                searchView.clearFocus();
                searchMenu.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchTerm = newText;
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
