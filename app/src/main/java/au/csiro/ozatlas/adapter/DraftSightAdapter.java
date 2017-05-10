package au.csiro.ozatlas.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import au.csiro.ozatlas.model.AddSight;
import au.csiro.ozatlas.model.Tag;

/**
 * Created by sad038 on 13/4/17.
 */

public class DraftSightAdapter extends RecyclerView.Adapter<DraftSightViewHolders> {

    private List<AddSight> sights;
    private boolean[] selection;
    private Context context;
    View.OnClickListener onClickListener;

    public DraftSightAdapter(List<AddSight> sights, Context context, View.OnClickListener onClickListener) {
        this.sights = sights;
        selection = new boolean[sights.size()];
        this.context = context;
        this.onClickListener = onClickListener;
    }

    public void selectionRefresh() {
        selection = new boolean[sights.size()];
    }

    public int getNumberOfSelectedSight(){
        int count = 0;
        for(boolean b:selection)
            if (b)
                count++;
         return count;
    }

    public ArrayList<Long> getPrimaryKeys(){
        ArrayList<Long> keys = new ArrayList<>();
        for(int i=0;i<sights.size();i++)
            if (selection[i])
                keys.add(sights.get(i).realmId);
        return keys;
    }

    @Override
    public DraftSightViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
        layoutView.setOnClickListener(onClickListener);
        layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new DraftSightViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final DraftSightViewHolders sightViewHolders, final int position) {
        sightViewHolders.checkBox.setVisibility(View.VISIBLE);
        if (selection[position])
            sightViewHolders.checkBox.setChecked(true);
        else
            sightViewHolders.checkBox.setChecked(false);
        sightViewHolders.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection[position] = !selection[position];
                sightViewHolders.checkBox.setChecked(selection[position]);
            }
        });
        AddSight sight = sights.get(position);
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
                }else{
                    sightViewHolders.image.setColorFilter(Color.GRAY);
                }
            }else{
                sightViewHolders.image.setColorFilter(Color.GRAY);
            }
        }else{
            sightViewHolders.image.setColorFilter(Color.GRAY);
        }
    }


    public static String tagJoin(CharSequence delimiter, List<Tag> tokens) {
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

class DraftSightViewHolders extends SightViewHolders {
    CheckBox checkBox;

    DraftSightViewHolders(View itemView) {
        super(itemView);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
    }
}

