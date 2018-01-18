package fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import au.csiro.ozatlas.R;

/**
 * Created by sad038 on 18/1/18.
 */

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int textViewResource;
    private String[] objects;

    public CustomSpinnerAdapter(@NonNull Context context, String[] objects, int resource) {
        super(context, resource, objects);
        mContext = context;
        textViewResource = resource;
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }


    public View getCustomView(int position, View convertView, ViewGroup parent, boolean dropDown) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(textViewResource, parent, false);
        TextView label = row.findViewById(android.R.id.text1);
        label.setText(objects[position]);
        if (dropDown)
            label.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        else
            label.setTextColor(Color.WHITE);
        return row;
    }
}
