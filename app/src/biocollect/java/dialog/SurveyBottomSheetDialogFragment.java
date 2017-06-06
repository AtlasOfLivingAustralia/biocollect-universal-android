package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import au.csiro.ozatlas.R;
import model.Survey;

/**
 * Created by sad038 on 5/6/17.
 */

public class SurveyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private ArrayList<Survey> surveys;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        //noinspection RestrictedApi
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_survey_bottom_sheet, null);
        dialog.setContentView(contentView);

        ListView listView = (ListView) contentView.findViewById(R.id.listView);

        Bundle bundle = getArguments();
        if (bundle != null)
            surveys = (ArrayList<Survey>) bundle.getSerializable(getString(R.string.survey_list_parameter));

        listView.setAdapter(new SurveyAdapter(getContext(), surveys));

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }


    public class SurveyAdapter extends ArrayAdapter<Survey> {
        // View lookup cache
        private class ViewHolder {
            TextView name;
        }

        public SurveyAdapter(Context context, ArrayList<Survey> surveys) {
            super(context, R.layout.item_survey, surveys);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Survey survey = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_survey, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.name.setText(survey.name);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
