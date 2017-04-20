package au.csiro.ozatlas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.Sight;

/**
 * Created by sad038 on 13/4/17.
 */

public class SightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int FOOTER = 2;
    private final int NORMAL = 1;
    private List<Sight> sights;
    private boolean needFooter;

    public SightAdapter(List<Sight> sights) {
        this.sights = sights;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
            layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            SightViewHolders sightViewHolders = (SightViewHolders) holder;
            Sight sight = sights.get(position);
            sightViewHolders.name.setText(sight.name);
            sightViewHolders.user.setText(sight.activityOwnerName);
            sightViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(sight.lastUpdated, "dd MMM, yyyy"));
            Glide.with(sightViewHolders.image.getContext())
                    .load(sight.thumbnailUrl)
                    .placeholder(R.drawable.ala_transparent)
                    .crossFade()
                    .into(sightViewHolders.image);
        }
    }

    @Override
    public int getItemCount() {
        if (needFooter)
            return sights.size() + 1;
        else
            return sights.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == sights.size())
            return FOOTER;
        else
            return NORMAL;

    }

    public void setNeedFooter(boolean needFooter) {
        this.needFooter = needFooter;
    }
}

class SightViewHolders extends RecyclerView.ViewHolder {
    TextView name, user, time;
    ImageView image;

    SightViewHolders(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        user = (TextView) itemView.findViewById(R.id.user);
        time = (TextView) itemView.findViewById(R.id.time);
        image = (ImageView) itemView.findViewById(R.id.image);
    }
}

class FooterViewHolders extends RecyclerView.ViewHolder {
    FooterViewHolders(View itemView) {
        super(itemView);
    }
}

