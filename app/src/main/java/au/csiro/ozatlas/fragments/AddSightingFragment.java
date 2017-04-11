package au.csiro.ozatlas.fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sad038 on 11/4/17.
 */

public class AddSightingFragment extends BaseFragment {
    private final int NUMBER_OF_INDIVIDUAL_LIMIT = 100;

    @BindView(R.id.individualSpinner)
    Spinner individualSpinner;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.date)
    TextView date;

    private String[] individualSpinnervalue = new String[NUMBER_OF_INDIVIDUAL_LIMIT];
    private ArrayAdapter<String> individualSpinnerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sight, container, false);
        ButterKnife.bind(this, view);

        makeIndividualLimit();
        // Create an ArrayAdapter using the string array and a default spinner layout
        individualSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, individualSpinnervalue);
        // Specify the layout to use when the list of choices appears
        individualSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        individualSpinner.setAdapter(individualSpinnerAdapter);
        Date now = new Date();
        time.setText(AtlasDateTimeUtils.getStringFromDate(now, "hh:mm a").toUpperCase());
        date.setText(AtlasDateTimeUtils.getStringFromDate(now, "dd MMMM, yyyy"));
        return view;
    }

    /**
     * making individual numbers from 1 to NUMBER_OF_INDIVIDUAL_LIMIT
     */
    private void makeIndividualLimit(){
        for(int i=1;i<=NUMBER_OF_INDIVIDUAL_LIMIT;i++){
            individualSpinnervalue[i-1]=String.valueOf(i);
        }
    }
}
