package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
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

import activity.SingleFragmentActivity;
import adapters.DraftSightAdapter;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import upload.UploadService;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the list of Sights saved locally
 * From Navigation Drawer -> Draft Shift
 */
public class DraftSightingListFragment extends BaseMainActivityFragment implements SwipeRefreshLayout.OnRefreshListener, MoreButtonListener {
    private final int REQUEST_EDIT = 1;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;


    private DraftSightAdapter sightAdapter;
    private List<AddSight> sights = new ArrayList<>();
    /**
     * onclick listener for recyclerview items
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Bundle bundle = new Bundle();
            //checking whether the item is being uploaded by the UploadService
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
    private Realm realm;
    /**
     * Long click listener for recyclerview items
     * for deleting a draft item
     */
    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View v) {
            delete(recyclerView.getChildAdapterPosition(v));
            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_refresh_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        setTitle(getString(R.string.draft_sighting_title));

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
        sightAdapter = new DraftSightAdapter(sights, getActivity(), onClickListener, onLongClickListener, this);
        recyclerView.setAdapter(sightAdapter);

        //refresh layout setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        //get the sighting
        readDraftSights();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("Draft Sighting List", TAG);
    }

    /**
     * show an alert for deleting an item.
     * deletes an item upon pressing "OK"
     *
     * @param position
     */
    private void delete(final int position) {
        AtlasDialogManager.alertBoxForSetting(getActivity(), getString(R.string.delete_sight_message), getString(R.string.delete_sight_title), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddSight addSight = sights.get(position);
                sights.remove(position);
                realm.beginTransaction();
                addSight.deleteFromRealm();
                realm.commitTransaction();
                sightAdapter.notifyDataSetChanged();
                updateTotal();
            }
        });
    }

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
                        /*if the user doesnot select any of the draft items,
                        it will show a dialog to get he permission to upload all the draft items.
                        */
                        if (sightAdapter.getNumberOfSelectedSight() == 0) {
                            AtlasDialogManager.alertBoxForSetting(getActivity(), getString(R.string.upload_message), "Upload", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadAll(true);
                                }
                            });
                        } else {
                            //upload only selected draft items
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

    /**
     * start the Upload Service
     *
     * @param all whether all the draft items or selected items
     */
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
                    //get the sightings again
                    readDraftSights();
                    break;
            }
        }
    }

    /**
     * Read all the AddSight RealmObjects saved locally
     */
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

    /**
     * updating the heading
     */
    private void updateTotal() {
        if (sights.size() > 1)
            total.setText(getString(R.string.draft_sighting_plural, sights.size()));
        else if (sights.size() == 1)
            total.setText(getString(R.string.draft_sighting_singular, sights.size()));
        else
            total.setText(getString(R.string.nothing_to_upload));
    }

    /**
     * refresh for swipetorefresh layout
     */
    @Override
    public void onRefresh() {
        readDraftSights();
    }

    /**
     * show popup menu from the more button of recyclerview items
     *
     * @param view
     * @param position
     */
    @Override
    public void onMoreButtonClick(View view, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.draft_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //do your things in each of the following cases
                switch (item.getItemId()) {
                    case R.id.delete:
                        delete(position);
                        break;
                    case R.id.upload:
                        Intent mServiceIntent = new Intent(getActivity(), UploadService.class);
                        ArrayList<Long> keys = new ArrayList<>();
                        keys.add(sights.get(position).realmId);
                        mServiceIntent.putExtra(getString(R.string.primary_keys_parameter), keys);
                        // Starts the IntentService to download the RSS feed data
                        getActivity().startService(mServiceIntent);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
