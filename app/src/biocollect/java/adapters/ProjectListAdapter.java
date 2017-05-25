package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.adapter.BaseRecyclerWithFooterViewAdapter;
import au.csiro.ozatlas.adapter.FooterViewHolders;
import au.csiro.ozatlas.model.Sight;
import model.Projects;

/**
 * Created by sad038 on 13/4/17.
 */

/**
 * This adapter is to show the projects from the server side
 * This adapter also show a Footer to load more items
 */
public class ProjectListAdapter extends BaseRecyclerWithFooterViewAdapter {

    private List<Projects> projectses;
    private View.OnClickListener onClickListener;

    /**
     * constructor
     *
     * @param projectses             projects to show
     * @param onClickListener    a click listener for the tap/single click listener
     */
    public ProjectListAdapter(List<Projects> projectses, View.OnClickListener onClickListener) {
        this.projectses = projectses;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //returning different view holders depending on viewType
        if (viewType == NORMAL) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sight, null);
            layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutView.setOnClickListener(onClickListener);
            return new ProjectViewHolders(layoutView);
        } else {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, null);
            layoutView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolders(layoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectViewHolders) {
            final ProjectViewHolders projectViewHolders = (ProjectViewHolders) holder;
            Projects projects = projectses.get(position);

            /*projectViewHolders.name.setText(project.projectName);
            projectViewHolders.type.setText(project.type);
            projectViewHolders.user.setText(project.activityOwnerName);
            projectViewHolders.time.setText(AtlasDateTimeUtils.getFormattedDayTime(project.lastUpdated, "dd MMM, yyyy"));
            Glide.with(projectViewHolders.image.getContext())
                    .load(getImageURL(project))
                    .placeholder(R.drawable.ala_transparent)
                    .crossFade()
                    .into(projectViewHolders.image);*/
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
            return projectses.size() + 1; // adding footer count
        else
            return projectses.size();
    }

    /**
     * if the position is equal to the sight list size then this is Footer
     * as usually the last position is projects.size()-1
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == projectses.size())
            return FOOTER;
        else
            return NORMAL;

    }
}

class ProjectViewHolders extends RecyclerView.ViewHolder {
    TextView name, user, time, type;
    ImageView image, moreButton;

    ProjectViewHolders(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        user = (TextView) itemView.findViewById(R.id.user);
        time = (TextView) itemView.findViewById(R.id.time);
        type = (TextView) itemView.findViewById(R.id.type);
        image = (ImageView) itemView.findViewById(R.id.image);
        moreButton = (ImageView) itemView.findViewById(R.id.more_button);
    }
}


