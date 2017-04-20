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

public class SightAdapter extends RecyclerView.Adapter<SightViewHolders> {

    private List<Sight> sights;

    public SightAdapter(List<Sight> sights) {
        this.sights = sights;
    }

    @Override
    public SightViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
        layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new SightViewHolders(layoutView);
    }

    @Override
    public int getItemCount() {
        return sights.size();
    }

    @Override
    public void onBindViewHolder(final SightViewHolders holder, final int position) {
        Sight sight = sights.get(position);
        holder.name.setText(sight.name);
        holder.name.setText(sight.activityOwnerName);
        holder.time.setText(AtlasDateTimeUtils.getFormattedDayTime(sight.lastUpdated, "dd MMM, yyyy"));
        Glide.with(holder.image.getContext())
                .load(sight.thumbnailUrl)
                .placeholder(R.drawable.ala_transparent)
                .crossFade()
                .into(holder.image);
    }
}

class SightViewHolders extends RecyclerView.ViewHolder {
    TextView name, user, time;
    ImageView image;

    public SightViewHolders(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        user = (TextView) itemView.findViewById(R.id.user);
        time = (TextView) itemView.findViewById(R.id.time);
        image = (ImageView) itemView.findViewById(R.id.image);
    }
}
