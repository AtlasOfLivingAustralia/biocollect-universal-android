package au.csiro.ozatlas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.csiro.ozatlas.R;

/**
 * Created by sad038 on 28/5/17.
 */

/**
 * This class is the viewholders for the sight items
 */
public class SightViewHolders extends RecyclerView.ViewHolder {
    public TextView name, user, time, type;
    public ImageView image, moreButton;

    public SightViewHolders(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        user = (TextView) itemView.findViewById(R.id.user);
        time = (TextView) itemView.findViewById(R.id.time);
        type = (TextView) itemView.findViewById(R.id.type);
        image = (ImageView) itemView.findViewById(R.id.image);
        moreButton = (ImageView) itemView.findViewById(R.id.more_button);
    }
}