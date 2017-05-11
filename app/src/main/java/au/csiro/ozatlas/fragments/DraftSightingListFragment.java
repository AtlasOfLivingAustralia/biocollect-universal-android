package au.csiro.ozatlas.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.upload.UploadService;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 13/4/17.
 */

public class DraftSightingListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "DraftSightingList";
    private final int REQUEST_EDIT = 1;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;


    private DraftSightAdapter sightAdapter;
    private List<AddSight> sights = new ArrayList<>();
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sight_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);


        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.showFloatingButton();

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        sightAdapter = new DraftSightAdapter(sights, getActivity(), onClickListener, onLongClickListener);
        recyclerView.setAdapter(sightAdapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting
        readDraftSights();

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Bundle bundle = new Bundle();
            if (sights.get(position).upLoading) {
                AtlasDialogManager.alertBoxForMessage(getActivity(), getString(R.string.currently_uploading_message), "OK");
            } else {
                bundle.putLong(getString(R.string.sight_parameter), sights.get(position).realmId);
                bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.EDIT_FRAGMENT);
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_EDIT);
            }
        }
    };


    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View v) {
            AtlasDialogManager.alertBoxForSetting(getActivity(), getString(R.string.delete_sight_message), getString(R.string.delete_sight_title), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    AddSight addSight = sights.get(position);
                    realm.beginTransaction();
                    addSight.deleteFromRealm();
                    realm.commitTransaction();
                    sights.remove(position);
                    sightAdapter.notifyDataSetChanged();
                    updateTotal();
                }
            });
            return true;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.upload, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //when the user will press the upload menu item
            case R.id.upload:
                if (AtlasManager.isNetworkAvailable(getActivity())) {

                    if (sights.size() > 0) {
                        if (sightAdapter.getNumberOfSelectedSight() == 0) {
                            AtlasDialogManager.alertBoxForSetting(getActivity(), getString(R.string.upload_message), "Upload", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadAll(true);
                                }
                            });
                        } else {
                            uploadAll(false);
                        }
                    } else {
                        showSnackBarMessage(getString(R.string.nothing_to_upload));
                    }
                } else {
                    showSnackBarMessage(getString(R.string.not_internet_title));
                }
                break;
        }
        return true;
    }

    private void uploadAll(boolean all) {
        Intent mServiceIntent = new Intent(getActivity(), UploadService.class);
        if (!all)
            mServiceIntent.putExtra(getString(R.string.primary_keys_parameter), sightAdapter.getPrimaryKeys());
        // Starts the IntentService to download the RSS feed data
        getActivity().startService(mServiceIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDIT:
                    //get the sighting
                    readDraftSights();
                    break;
            }
        }
    }

    public void readDraftSights() {
        RealmResults<AddSight> results = realm.where(AddSight.class).findAllAsync();
        results.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<AddSight>>() {
            @Override
            public void onChange(RealmResults<AddSight> collection, OrderedCollectionChangeSet changeSet) {
                sights.clear();
                sights.addAll(collection);
                sightAdapter.selectionRefresh();
                updateTotal();
                sightAdapter.notifyDataSetChanged();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void updateTotal(){
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
