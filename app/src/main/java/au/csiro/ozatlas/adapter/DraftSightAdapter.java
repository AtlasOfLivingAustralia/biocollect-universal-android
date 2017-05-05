package au.csiro.ozatlas.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Iterator;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.Sight;
import au.csiro.ozatlas.model.Tag;

/**
 * Created by sad038 on 13/4/17.
 */

public class DraftSightAdapter extends RecyclerView.Adapter<DraftSightViewHolders> {

    private List<AddSight> sights;
    private Context context;

    public DraftSightAdapter(List<AddSight> sights, Context context) {
        this.sights = sights;
        this.context = context;
    }

    @Override
    public DraftSightViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
        layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new DraftSightViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(DraftSightViewHolders sightViewHolders, int position) {
        sightViewHolders.checkBox.setVisibility(View.VISIBLE);

        AddSight sight = sights.get(position);
        if (sight.outputs != null && sight.outputs.size() > 0) {
            sightViewHolders.name.setText(sight.outputs.get(0).name);
            if (sight.outputs.get(0).data != null) {
                sightViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(sight.outputs.get(0).data.surveyDate, "dd MMM, yyyy"));
                sightViewHolders.user.setText(context.getString(R.string.tags_name, tagJoin(", ", sight.outputs.get(0).data.tags)));
                if (sight.outputs.get(0).data.species != null) {
                    sightViewHolders.type.setText(context.getString(R.string.species_name, sight.outputs.get(0).data.species.name == null ? "" : sight.outputs.get(0).data.species.name));
                }
                if (sight.outputs.get(0).data.sightingPhoto != null && sight.outputs.get(0).data.sightingPhoto.size() > 0) {
                    Glide.with(context)
                            .load(sight.outputs.get(0).data.sightingPhoto.get(0).filePath)
                            .placeholder(R.drawable.ala_transparent)
                            .crossFade()
                            .into(sightViewHolders.image);
                }
            }
        }
    }

    public static String tagJoin(CharSequence delimiter, List<Tag> tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<Tag> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
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

class DraftSightViewHolders extends SightViewHolders {
    CheckBox checkBox;

    DraftSightViewHolders(View itemView) {
        super(itemView);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
    }
}

