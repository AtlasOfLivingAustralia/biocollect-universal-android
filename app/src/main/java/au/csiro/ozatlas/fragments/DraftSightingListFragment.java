package au.csiro.ozatlas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
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
import au.csiro.ozatlas.activity.SingleFragmentActivity;
import au.csiro.ozatlas.adapter.DraftSightAdapter;
import au.csiro.ozatlas.adapter.SightAdapter;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.listener.RecyclerItemClickListener;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.Sight;
import au.csiro.ozatlas.model.SightList;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;

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


    private DraftSightAdapter sightAdapter;
    private List<AddSight> sights = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sight_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        mainActivityFragmentListener.showFloatingButton();

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        sightAdapter = new DraftSightAdapter(sights, getActivity());
        recyclerView.setAdapter(sightAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.sight_parameter), realm.copyToRealm(sights.get(position)));
                        bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.EDIT_FRAGMENT);
                        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
        );

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting
        readDraftSights();

        return view;
    }

    private void readDraftSights(){
        sights.clear();
        sights.addAll(realm.where(AddSight.class).findAll());
        total.setText(getString(R.string.total_sighting, sights.size()));
    }

    @Override
    public void onRefresh() {
        readDraftSights();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
