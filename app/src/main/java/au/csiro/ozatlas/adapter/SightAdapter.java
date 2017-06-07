package au.csiro.ozatlas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseRecyclerWithFooterViewAdapter;
import au.csiro.ozatlas.base.MoreButtonListener;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.Sight;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This adapter is to show the sights from the server side
 * This adapter also show a Footer to load more items
 */
public class SightAdapter extends BaseRecyclerWithFooterViewAdapter {

    private List<Sight> sights;
    private MoreButtonListener moreButtonListener;
    private View.OnClickListener onClickListener;
    private boolean isShowMoreButton = false;

    /**
     * constructor
     *
     * @param sights             sights to show
     * @param onClickListener    a click listener for the tap/single click listener
     * @param moreButtonListener a click listener for showing the popup menu
     */
    public SightAdapter(List<Sight> sights, View.OnClickListener onClickListener, MoreButtonListener moreButtonListener, String myRecords) {
        this.sights = sights;
        this.moreButtonListener = moreButtonListener;
        this.onClickListener = onClickListener;
        isShowMoreButton = myRecords != null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //returning different view holders depending on viewType
        if (viewType == NORMAL) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
            layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutView.setOnClickListener(onClickListener);
            return new SightViewHolders(layoutView);
        } else {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, null);
            layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolders(layoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SightViewHolders) {
            final SightViewHolders sightViewHolders = (SightViewHolders) holder;
            Sight sight = sights.get(position);
            if (isShowMoreButton) {
                sightViewHolders.moreButton.setVisibility(View.VISIBLE);
                sightViewHolders.moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (moreButtonListener != null)
                            moreButtonListener.onPopupMenuClick(sightViewHolders.moreButton, sightViewHolders.getAdapterPosition());
                    }
                });
            }
            sightViewHolders.name.setText(sight.projectName);
            sightViewHolders.type.setText(sight.type);
            sightViewHolders.user.setText(sight.activityOwnerName);
            sightViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(sight.lastUpdated, "dd MMM, yyyy"));
            Glide.with(sightViewHolders.image.getContext())
                    .load(getImageURL(sight))
                    .placeholder(R.drawable.ala_transparent)
                    .crossFade()
                    .into(sightViewHolders.image);
        }
    }

    /**
     * @param sight
     * @return an image url if the sight object has any
     */
    private String getImageURL(Sight sight) {
        if (sight.records != null && sight.records.length > 0 && sight.records[0].multimedia != null && sight.records[0].multimedia.length > 0) {
            return sight.records[0].multimedia[0].identifier;
        }
        return sight.thumbnailUrl;
    }

    /**
     * @return an extra item if the needFooter (for showing the footer) is enabled
     */
    @Override
    public int getItemCount() {
        if (needFooter)
            return sights.size() + 1; // adding footer count
        else
            return sights.size();
    }

    /**
     * if the position is equal to the sight list size then this is Footer
     * as usually the last position is sights.size()-1
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == sights.size())
            return FOOTER;
        else
            return NORMAL;

    }
}


