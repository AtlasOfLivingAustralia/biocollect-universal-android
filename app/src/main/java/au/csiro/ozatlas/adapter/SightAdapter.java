package au.csiro.ozatlas.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

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

    /**
     * constructor
     *
     * @param sights             sights to show
     * @param onClickListener    a click listener for the tap/single click listener
     * @param moreButtonListener a click listener for showing the popup menu
     */
    public SightAdapter(List<Sight> sights, View.OnClickListener onClickListener, MoreButtonListener moreButtonListener) {
        this.sights = sights;
        this.moreButtonListener = moreButtonListener;
        this.onClickListener = onClickListener;
        //isShowMoreButton = (myRecords != null && myRecords.equals("myrecords")); //|| !(myRecords != null && myRecords.equals("project"));
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
            if (sight.showCrud) {
                sightViewHolders.moreButton.setVisibility(View.VISIBLE);
                sightViewHolders.moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (moreButtonListener != null)
                            moreButtonListener.onMoreButtonClick(sightViewHolders.moreButton, sightViewHolders.getAdapterPosition());
                    }
                });
            } else {
                sightViewHolders.moreButton.setVisibility(View.INVISIBLE);
            }
            sightViewHolders.name.setText(sight.projectName);
            sightViewHolders.type.setText(sight.type == null ? sight.name : sight.type);
            sightViewHolders.user.setText(sight.activityOwnerName);
            sightViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(sight.lastUpdated, "dd MMM, yyyy"));
            sightViewHolders.image.setColorFilter(Color.WHITE);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.no_image_available)
                    .error(R.drawable.no_image_available);
            Glide.with(sightViewHolders.image.getContext())
                    .load(getImageURL(sight))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            sightViewHolders.image.clearColorFilter();
                            sightViewHolders.image.setImageDrawable(resource);
                            return false;
                        }
                    }).apply(options)
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


