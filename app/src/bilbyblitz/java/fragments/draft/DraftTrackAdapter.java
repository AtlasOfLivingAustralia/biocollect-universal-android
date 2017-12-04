package fragments.draft;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.SightViewHolders;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.Tag;
import model.track.TrackModel;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * a RecyclerView.Adapter<DraftTrackViewHolders> for showing the DraftSights
 */
public class DraftTrackAdapter extends RecyclerView.Adapter<DraftTrackViewHolders> {

    private List<TrackModel> trackModels;
    private boolean[] selection;
    private Context context;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private MoreButtonListener moreButtonListener;

    /**
     * constructor
     *
     * @param trackModels              trackModels to show
     * @param context
     * @param onClickListener     a click listener for the checkbox
     * @param onLongClickListener a long click listener to delete the draft sight
     * @param moreButtonListener  a click listener for the popup menu items
     */
    public DraftTrackAdapter(List<TrackModel> trackModels, Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, MoreButtonListener moreButtonListener) {
        this.trackModels = trackModels;
        selection = new boolean[trackModels.size()];
        this.context = context;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        this.moreButtonListener = moreButtonListener;
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
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
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

            trackViewHolders.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection[trackViewHolders.getAdapterPosition()] = !selection[trackViewHolders.getAdapterPosition()];
                    trackViewHolders.checkBox.setChecked(selection[trackViewHolders.getAdapterPosition()]);
                }
            });
        }

        trackViewHolders.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreButtonListener != null)
                    moreButtonListener.onMoreButtonClick(trackViewHolders.moreButton, trackViewHolders.getAdapterPosition());
            }
        });

        if (trackModel.isValid() && trackModel.outputs != null && trackModel.outputs.size() > 0) {
            trackViewHolders.name.setText(trackModel.outputs.get(0).name);
            if (trackModel.outputs.get(0).data != null) {
                trackViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(trackModel.outputs.get(0).data.surveyDate, "dd MMM, yyyy"));
                trackViewHolders.user.setText(trackModel.outputs.get(0).data.recordedBy);
                /*if (trackModel.outputs.get(0).data.species != null) {
                    trackViewHolders.type.setText(context.getString(R.string.species_name, trackModel.outputs.get(0).data.species.name == null ? "" : trackModel.outputs.get(0).data.species.name));
                }
                if (trackModel.outputs.get(0).data.sightingPhoto != null && trackModel.outputs.get(0).data.sightingPhoto.size() > 0) {
                    trackViewHolders.image.clearColorFilter();
                    Glide.with(context)
                            .load(trackModel.outputs.get(0).data.sightingPhoto.get(0).filePath)
                            .placeholder(R.drawable.ala_transparent)
                            .crossFade()
                            .into(trackViewHolders.image);
                } else {
                    trackViewHolders.image.setColorFilter(Color.GRAY);
                }*/
            } else {
                trackViewHolders.image.setColorFilter(Color.GRAY);
            }
        } else {
            trackViewHolders.image.setColorFilter(Color.GRAY);
        }
    }

    /**
     * join the Tags' val with the given delimeter
     *
     * @param delimiter
     * @param tokens
     * @return
     */
    private String tagJoin(CharSequence delimiter, List<Tag> tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<Tag> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next().val);
            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next().val);
            }
        }
        return sb.toString();
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

