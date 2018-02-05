package fragments.draft;

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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import activity.SingleFragmentActivity;
import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.AtlasManager;
import au.csiro.ozatlas.manager.Language;
import au.csiro.ozatlas.view.ItemOffsetDecoration;
import base.BaseMainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import model.track.TrackModel;
import upload.UploadService;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This class is to show the list of Tracks saved locally
 * From Navigation Drawer -> Draft Shift
 */
public class DraftTrackListFragment extends BaseMainActivityFragment implements SwipeRefreshLayout.OnRefreshListener, MoreButtonListener {
    private final int REQUEST_EDIT = 1;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.total)
    TextView total;

    private DraftTrackAdapter sightAdapter;
    private List<TrackModel> trackModels = new ArrayList<>();
    /**
     * onclick listener for recyclerview items
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            TrackModel trackModel = realm.copyFromRealm(trackModels.get(position));
            Bundle bundle = new Bundle();
            //checking whether the item is being uploaded by the UploadService
            if (trackModel.upLoading) {
                AtlasDialogManager.alertBoxForMessage(getActivity(), getString(R.string.currently_uploading_message), "OK");
            } else {
                bundle.putLong(getString(R.string.primary_key_parameter), trackModel.realmId);
                bundle.putSerializable(getString(R.string.fragment_type_parameter), SingleFragmentActivity.FragmentType.EDIT_TRACK_FRAGMENT);
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_EDIT);
            }
        }
    };

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
        setTitle(getString(R.string.review_track));
        setHasOptionsMenu(true);

        if (mainActivityFragmentListener != null)
            mainActivityFragmentListener.hideFloatingButton();

        //recyclerView setup
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_item_margin);
        recyclerView.addItemDecoration(itemDecoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        sightAdapter = new DraftTrackAdapter(trackModels, getActivity(), onClickListener, onLongClickListener, this);
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
        AtlasDialogManager.alertBox(getActivity(), getString(R.string.delete_sight_message), getString(R.string.delete_sight_title), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TrackModel trackModel = trackModels.get(position);
                trackModels.remove(position);
                realm.beginTransaction();
                trackModel.deleteFromRealm();
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
                    if (trackModels.size() > 0) {
                        /*if the user doesnot select any of the draft items,
                        it will show a dialog to get he permission to upload all the draft items.
                        */
                        if (sightAdapter.getNumberOfSelectedSight() == 0) {
                            AtlasDialogManager.alertBox(getActivity(), getString(R.string.upload_message), "Upload", new DialogInterface.OnClickListener() {
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
        RealmResults<TrackModel> results = realm.where(TrackModel.class).findAllAsync();
        results.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<TrackModel>>() {
            @Override
            public void onChange(RealmResults<TrackModel> collection, OrderedCollectionChangeSet changeSet) {
                trackModels.clear();
                trackModels.addAll(collection);
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
        if (trackModels.size() > 1)
            total.setText(getString(R.string.draft_track_plural, trackModels.size()));
        else if (trackModels.size() == 1)
            total.setText(getString(R.string.draft_track_singular, trackModels.size()));
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
                        keys.add(trackModels.get(position).realmId);
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
    protected void setLanguageValues(Language language) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadService.UploadNotification uploadNotification) {
        if (isAdded())
            readDraftSights();
    }

}
