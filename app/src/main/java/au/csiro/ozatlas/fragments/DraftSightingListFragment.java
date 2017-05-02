package au.csiro.ozatlas.fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.listener.RecyclerItemClickListener;
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

public class DraftSightingListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "DraftSightingListFragment";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;


    private SightAdapter sightAdapter;
    private List<Sight> sights = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sight_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        mainActivityFragmentListener.showFloatingButton();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        sightAdapter = new SightAdapter(sights);
        recyclerView.setAdapter(sightAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }
                })
        );

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting

        return view;
    }



    @Override
    public void onRefresh() {

    }
}
