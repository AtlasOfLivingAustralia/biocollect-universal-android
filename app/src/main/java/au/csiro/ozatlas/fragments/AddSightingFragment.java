package au.csiro.ozatlas.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.manager.AtlasDateTimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sad038 on 11/4/17.
 */

public class AddSightingFragment extends BaseFragment {
    private final int NUMBER_OF_INDIVIDUAL_LIMIT = 100;

    @BindView(R.id.individualSpinner)
    Spinner individualSpinner;
    @BindView(R.id.identificationTagSpinner)
    Spinner identificationTagSpinner;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.date)
    TextView date;

    private String[] individualSpinnerValue = new String[NUMBER_OF_INDIVIDUAL_LIMIT];
    private ArrayAdapter<String> individualSpinnerAdapter;
    private ArrayAdapter<String> tagsSpinnerAdapter;
    private Calendar now = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sight, container, false);
        ButterKnife.bind(this, view);

        //hiding the floating action button
        floatingActionButtonListener.hideFloatingButton();

        makeIndividualLimit();
        // Create an ArrayAdapter using the string array and a default spinner layout
        individualSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, individualSpinnerValue);
        // Specify the layout to use when the list of choices appears
        individualSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        individualSpinner.setAdapter(individualSpinnerAdapter);

        //setting the date
        time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "hh:mm a").toUpperCase());
        date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "dd MMMM, yyyy"));

        mCompositeDisposable.add(getFileReadObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        Log.d("", value);
                        tagsSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, createTagLists(value));
                        tagsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        identificationTagSpinner.setAdapter(tagsSpinnerAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
        return view;
    }

    private List<String> createTagLists(String json){
        List<String> tags = new ArrayList<>();
        Set<String> set = new HashSet<>();

        try {
            JSONObject jObject = new JSONObject(json);
            Iterator<?> keys = jObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                String value = jObject.getString(key);
                if(!set.contains(value)){
                    tags.add(value);
                    set.add(value);
                }
                tags.add(value.concat(" - ").concat(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tags;
    }

    /**
     * Observable to read the tag.txt file
     * @return
     */
    private Observable<String> getFileReadObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(readAsset("tags.txt"));
            }
        });
    }

    /**
     * making individual numbers from 1 to NUMBER_OF_INDIVIDUAL_LIMIT
     */
    private void makeIndividualLimit() {
        for (int i = 1; i <= NUMBER_OF_INDIVIDUAL_LIMIT; i++) {
            individualSpinnerValue[i - 1] = String.valueOf(i);
        }
    }

    @OnClick(R.id.time)
    public void time() {
        (new TimePickerDialog(getActivity(), R.style.DateTimeDialogTheme, timeSetListener, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false)).show();
    }

    /**
     * Time picker Listener
     */
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            now.set(Calendar.HOUR_OF_DAY, hourOfDay);
            now.set(Calendar.MINUTE, minute);
            time.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "hh:mm a").toUpperCase());
        }
    };

    @OnClick(R.id.date)
    public void date() {
        (new DatePickerDialog(getActivity(), R.style.DateTimeDialogTheme, onDateSetListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))).show();
    }

    /**
     * Date Picker Listener
     */
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            now.set(Calendar.YEAR, year);
            now.set(Calendar.MONTH, month);
            now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(AtlasDateTimeUtils.getStringFromDate(now.getTime(), "dd MMMM, yyyy"));
        }
    };

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
