package au.csiro.ozatlas.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.Tag;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * a RecyclerView.Adapter<DraftSightViewHolders> for showing the DraftSights
 */
public class DraftSightAdapter extends RecyclerView.Adapter<DraftSightViewHolders> {

    private List<AddSight> sights;
    private boolean[] selection;
    private Context context;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private MoreButtonListener moreButtonListener;

    /**
     * constructor
     *
     * @param sights              sights to show
     * @param context
     * @param onClickListener     a click listener for the checkbox
     * @param onLongClickListener a long click listener to delete the draft sight
     * @param moreButtonListener  a click listener for the popup menu items
     */
    public DraftSightAdapter(List<AddSight> sights, Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, MoreButtonListener moreButtonListener) {
        this.sights = sights;
        selection = new boolean[sights.size()];
        this.context = context;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        this.moreButtonListener = moreButtonListener;
    }

    /**
     * refreshing the checkbox selection
     */
    public void selectionRefresh() {
        selection = new boolean[sights.size()];
    }

    /**
     * @return the number of checked sights
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
        for (int i = 0; i < sights.size(); i++)
            if (selection[i])
                keys.add(sights.get(i).realmId);
        return keys;
    }

    @Override
    public DraftSightViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
        layoutView.setOnClickListener(onClickListener);
        layoutView.setOnLongClickListener(onLongClickListener);
        layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new DraftSightViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final DraftSightViewHolders sightViewHolders, int position) {
        AddSight sight = sights.get(position);
        //if the sight is being uploaded then show the upload image or show the checkbox
        if (sight.upLoading) {
            sightViewHolders.uploadImage.setVisibility(View.VISIBLE);
            sightViewHolders.checkBox.setVisibility(View.INVISIBLE);
            sightViewHolders.moreButton.setOnClickListener(null);
        } else {
            sightViewHolders.uploadImage.setVisibility(View.INVISIBLE);
            sightViewHolders.checkBox.setVisibility(View.VISIBLE);
            if (selection[position])
                sightViewHolders.checkBox.setChecked(true);
            else
                sightViewHolders.checkBox.setChecked(false);

            sightViewHolders.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection[sightViewHolders.getAdapterPosition()] = !selection[sightViewHolders.getAdapterPosition()];
                    sightViewHolders.checkBox.setChecked(selection[sightViewHolders.getAdapterPosition()]);
                }
            });
        }

        sightViewHolders.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreButtonListener != null)
                    moreButtonListener.onPopupMenuClick(sightViewHolders.moreButton, sightViewHolders.getAdapterPosition());
            }
        });

        if (sight.isValid() && sight.outputs != null && sight.outputs.size() > 0) {
            sightViewHolders.name.setText(sight.outputs.get(0).name);
            if (sight.outputs.get(0).data != null) {
                sightViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(sight.outputs.get(0).data.surveyDate, "dd MMM, yyyy"));
                sightViewHolders.user.setText(context.getString(R.string.tags_name, tagJoin(", ", sight.outputs.get(0).data.tags)));
                if (sight.outputs.get(0).data.species != null) {
                    sightViewHolders.type.setText(context.getString(R.string.species_name, sight.outputs.get(0).data.species.name == null ? "" : sight.outputs.get(0).data.species.name));
                }
                if (sight.outputs.get(0).data.sightingPhoto != null && sight.outputs.get(0).data.sightingPhoto.size() > 0) {
                    sightViewHolders.image.clearColorFilter();
                    Glide.with(context)
                            .load(sight.outputs.get(0).data.sightingPhoto.get(0).filePath)
                            .placeholder(R.drawable.ala_transparent)
                            .crossFade()
                            .into(sightViewHolders.image);
                } else {
                    sightViewHolders.image.setColorFilter(Color.GRAY);
                }
            } else {
                sightViewHolders.image.setColorFilter(Color.GRAY);
            }
        } else {
            sightViewHolders.image.setColorFilter(Color.GRAY);
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
        return sights.size();
    }
}

/**
 * View Holders for DraftSights
 */
class DraftSightViewHolders extends SightViewHolders {
    CheckBox checkBox;
    ImageView uploadImage;

    DraftSightViewHolders(View itemView) {
        super(itemView);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        uploadImage = (ImageView) itemView.findViewById(R.id.uploadImage);
    }
}

