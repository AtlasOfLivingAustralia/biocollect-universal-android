package au.csiro.ozatlas.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;

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
    private Calendar now = Calendar.getInstance();

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

        //setting the date
        time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "hh:mm a").toUpperCase());
        date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "dd MMMM, yyyy"));

        return view;
    }

    /**
     * making individual numbers from 1 to NUMBER_OF_INDIVIDUAL_LIMIT
     */
    private void makeIndividualLimit() {
        for (int i = 1; i <= NUMBER_OF_INDIVIDUAL_LIMIT; i++) {
            individualSpinnervalue[i - 1] = String.valueOf(i);
        }
    }

    @OnClick(R.id.time)
    public void time() {
        (new TimePickerDialog(getActivity(), timeSetListener, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false)).show();
    }

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            now.set(Calendar.HOUR_OF_DAY, hourOfDay);
            now.set(Calendar.MINUTE, minute);
            time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "hh:mm a").toUpperCase());
        }
    };

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            now.set(Calendar.YEAR, year);
            now.set(Calendar.MONTH, month);
            now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "dd MMMM, yyyy"));
        }
    };

    @OnClick(R.id.date)
    public void date() {
        (new DatePickerDialog(getActivity(), onDateSetListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))).show();
    }

    /**
     * Reads the text of an asset. Should not be run on the UI thread.
     *
     * @param path The path to the asset.
     * @return The plain text of the asset
     */
    public String readAsset(String path) {
        String contents = "";
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = getActivity().getAssets().open(path);
            reader = new BufferedReader(new InputStreamReader(is));
            contents = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                contents += '\n' + line;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return contents;
    }

}
