package au.csiro.ozatlas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.model.SearchSpecies;
import au.csiro.ozatlas.model.SpeciesSearchResponse;

/**
 * Created by sad038 on 18/4/17.
 */

/**
 * This adapter is to show species suggestion
 * for adding/editing sight
 */
public class SearchSpeciesAdapter extends ArrayAdapter {
    private List<SearchSpecies> species;
    private LayoutInflater inflater;

    public SearchSpeciesAdapter(@NonNull Context context, List<SearchSpecies> species) {
        super(context, R.layout.item_search, species);
        this.species = species;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public String getItem(int position) {
        return species.get(position).name;
    }

    @Override
    public int getCount() {
        return species.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.item_search, parent, false);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) rowView.findViewById(R.id.content);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textView.setText(Html.fromHtml(species.get(position).name));
        return rowView;
    }

    private class ViewHolder {
        TextView textView;
    }

}
