package au.csiro.ozatlas.manager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;

import au.csiro.ozatlas.R;

/**
 * Created by sad038 on 24/11/17.
 */

public class AtlasDateTimeDialogManager {
    public static void showDatePicker(Context context, DatePickerDialog.OnDateSetListener onDateSetListener, int year, int month, int dayOfMonth) {
        (new DatePickerDialog(context, R.style.DateTimeDialogTheme, onDateSetListener, year, month, dayOfMonth)).show();
    }

    public static void showDatePicker(Context context, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar now = Calendar.getInstance();
        showDatePicker(context, onDateSetListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    }

    public static void showTimePicker(Context context, TimePickerDialog.OnTimeSetListener timeSetListener, int hour, int minute, boolean is24HourShow) {
        (new TimePickerDialog(context, R.style.DateTimeDialogTheme, timeSetListener, hour, minute, is24HourShow)).show();
    }

    public static void showTimePicker(Context context, TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar now = Calendar.getInstance();
        showTimePicker(context, timeSetListener, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false);
    }
}
