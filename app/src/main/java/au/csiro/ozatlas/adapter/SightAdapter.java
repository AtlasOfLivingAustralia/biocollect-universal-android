package au.csiro.ozatlas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.csiro.ozatlas.R;
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
    }
}

class SightViewHolders extends RecyclerView.ViewHolder {
    TextView name;

    public SightViewHolders(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
    }
}
