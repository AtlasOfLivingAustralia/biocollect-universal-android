package fragments.draft;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SightViewHolders;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.manager.FileUtils;
import io.realm.Realm;
import model.track.BilbyBlitzOutput;
import model.track.SightingEvidenceTable;
import model.track.TrackModel;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * a RecyclerView.Adapter<DraftTrackViewHolders> for showing the DraftSights
 */
public class DraftTrackAdapter extends RecyclerView.Adapter<DraftTrackViewHolders> {

    Realm realm;
    private List<TrackModel> trackModels;
    private boolean[] selection;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private MoreButtonListener moreButtonListener;

    /**
     * constructor
     *
     * @param trackModels         trackModels to show
     * @param onClickListener     a click listener for the checkbox
     * @param onLongClickListener a long click listener to delete the draft sight
     * @param moreButtonListener  a click listener for the popup menu items
     */
    public DraftTrackAdapter(List<TrackModel> trackModels, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, MoreButtonListener moreButtonListener) {
        this.trackModels = trackModels;
        selection = new boolean[trackModels.size()];
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        this.moreButtonListener = moreButtonListener;
        realm = Realm.getDefaultInstance();
    }

    /**
     * refreshing the checkbox selection
     */
    public void selectionRefresh() {
        selection = new boolean[trackModels.size()];
    }

    /**
     * @return the number of checked trackModels
     */
    public int getNumberOfSelectedSight() {
        int count = 0;
        for (boolean b : selection)
            if (b)
                count++;
        return count;
    }

    /**
     * @return a list of primary key which are checked by the users
     */
    public ArrayList<Long> getPrimaryKeys() {
        ArrayList<Long> keys = new ArrayList<>();
        for (int i = 0; i < trackModels.size(); i++)
            if (selection[i])
                keys.add(trackModels.get(i).realmId);
        return keys;
    }

    @Override
    public DraftTrackViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, null);
        layoutView.setOnClickListener(onClickListener);
        layoutView.setOnLongClickListener(onLongClickListener);
        layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new DraftTrackViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final DraftTrackViewHolders trackViewHolders, int position) {
        TrackModel trackModel = trackModels.get(position);
        //if the trackModel is being uploaded then show the upload image or show the checkbox
        if (trackModel.upLoading) {
            trackViewHolders.uploadImage.setVisibility(View.VISIBLE);
            trackViewHolders.checkBox.setVisibility(View.INVISIBLE);
            trackViewHolders.moreButton.setOnClickListener(null);
        } else {
            trackViewHolders.uploadImage.setVisibility(View.INVISIBLE);
            trackViewHolders.checkBox.setVisibility(View.VISIBLE);
            if (selection[position])
                trackViewHolders.checkBox.setChecked(true);
            else
                trackViewHolders.checkBox.setChecked(false);

            trackViewHolders.checkBox.setOnClickListener(v -> {
                selection[trackViewHolders.getAdapterPosition()] = !selection[trackViewHolders.getAdapterPosition()];
                trackViewHolders.checkBox.setChecked(selection[trackViewHolders.getAdapterPosition()]);
            });
        }

        trackViewHolders.moreButton.setOnClickListener(v -> {
            if (moreButtonListener != null)
                moreButtonListener.onMoreButtonClick(trackViewHolders.moreButton, trackViewHolders.getAdapterPosition());
        });

        if (trackModel.isValid() && trackModel.outputs != null && trackModel.outputs.size() > 0) {
            BilbyBlitzOutput output = trackModel.outputs.first();
            if (output != null && output.data != null) {
                trackViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(output.data.surveyDate, "dd MMM, yyyy"));
                trackViewHolders.user.setText(output.data.recordedBy);
                trackViewHolders.name.setText(trackModel.outputs.get(0).data.organisationName);
                if (output.data.sightingEvidenceTable != null) {
                    trackViewHolders.type.setText("");
                    for (SightingEvidenceTable sightingEvidenceTable : output.data.sightingEvidenceTable)
                        if (sightingEvidenceTable.species != null)
                            trackViewHolders.type.append(sightingEvidenceTable.species.vernacularName + ", ");

                    if(!TextUtils.isEmpty(trackViewHolders.type.getText())){
                        trackViewHolders.type.setText(trackViewHolders.type.getText().subSequence(0, trackViewHolders.type.getText().length()-2));
                    }

                    if (output.data.sightingEvidenceTable.size() > 0) {
                        SightingEvidenceTable sightingEvidenceTable = output.data.sightingEvidenceTable.first();
                        if ((sightingEvidenceTable != null ? sightingEvidenceTable.mPhotoPath : null) != null) {
                            trackViewHolders.image.clearColorFilter();
                            trackViewHolders.image.setImageBitmap(FileUtils.getBitmapFromFilePath(sightingEvidenceTable.mPhotoPath));
                        }else{
                            trackViewHolders.image.setColorFilter(Color.WHITE);
                        }
                    }else
                        trackViewHolders.image.setColorFilter(Color.WHITE);
                }else {
                    trackViewHolders.image.setColorFilter(Color.WHITE);
                    trackViewHolders.type.setText("");
                }
            } else {
                trackViewHolders.image.setColorFilter(Color.WHITE);
            }
        } else {
            trackViewHolders.image.setColorFilter(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return trackModels.size();
    }
}

/**
 * View Holders for DraftSights
 */
class DraftTrackViewHolders extends SightViewHolders {
    CheckBox checkBox;
    ImageView uploadImage;

    DraftTrackViewHolders(View itemView) {
        super(itemView);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        uploadImage = (ImageView) itemView.findViewById(R.id.uploadImage);
        moreButton.setVisibility(View.VISIBLE);
    }
}

