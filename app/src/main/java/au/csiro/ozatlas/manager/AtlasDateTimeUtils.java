package au.csiro.ozatlas.manager;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by najmussadat on 1/09/2015.
 */
public class AtlasDateTimeUtils {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //2017-04-18T15:07:40Z
    public static final String DEFAULT_TIME_FORMAT = "hh:mm a";
    private final static String TAG = "AtlasDateTimeUtils";

    /**
     * get current time with given format
     *
     * @param format
     * @return
     */
    public static String getCurrentTime(String format) {
        return getSimpleFormatter(format).format(Calendar.getInstance().getTime());
    }

    /**
     * get current time with default format
     *
     * @return
     */
    public static String getCurrentTime() {
        return getCurrentTime(DEFAULT_DATE_FORMAT);
    }

    /**
     * format the date string to another date string
     *
     * @param dateString
     * @param senderFormat
     * @param expectedFormat
     * @return
     */
    public static String getFormattedDayTime(String dateString, String senderFormat, String expectedFormat) {
        SimpleDateFormat sdf = getSimpleFormatter(senderFormat);
        try {
            return getSimpleFormatter(expectedFormat).format(sdf.parse(dateString)).replace(".", "");
        } catch (ParseException p) {
            Log.d(TAG, p.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return "";
    }

    /**
     * format the date string to another date string
     *
     * @param dateString
     * @param senderFormat
     * @param expectedFormat
     * @param senderTZ       timezone
     * @param expectedTZ     timezone
     * @return
     */
    public static String getFormattedDayTime(String dateString, String senderFormat, String expectedFormat, TimeZone senderTZ, TimeZone expectedTZ) {
        try {
            SimpleDateFormat senderSDF = getSimpleFormatter(senderFormat);
            senderSDF.setTimeZone(senderTZ);
            Date date = senderSDF.parse(dateString);
            SimpleDateFormat expectedSDF = getSimpleFormatter(expectedFormat);
            expectedSDF.setTimeZone(expectedTZ);
            return expectedSDF.format(date);
        } catch (ParseException p) {
            Log.d(TAG, p.getMessage());
        }
        return "";
    }

    /**
     * format the date string to another date string
     * dateString is expected in default format
     *
     * @param dateString
     * @param format
     * @return
     */
    public static String getFormattedDayTime(String dateString, String format) {
        String s = getFormattedDayTime(dateString, DEFAULT_DATE_FORMAT, format);
        return s;
    }

    /**
     * make Date object from a date String
     *
     * @param date
     * @param format
     * @return
     */
    public static Date getDateFromString(String date, String format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());

            return formatter.parse(date);
        } catch (Exception ex) {
            Log.d(TAG, "Errors in getDateFromString:" + ex);
            return null;
        }
    }

    /**
     * make Date object from a date String
     * date string is expected to be in default format
     *
     * @param date
     * @return
     */
    public static Date getDateFromString(String date) {
        return getDateFromString(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * get formatted string from a Date
     *
     * @param date
     * @param format
     * @return
     */
    public static String getStringFromDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = getSimpleFormatter(format);
        return formatter.format(date);
    }

    /**
     * get formatted string from a Date
     *
     * @param date
     * @return
     */
    public static String getStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = getSimpleFormatter(DEFAULT_DATE_FORMAT);
        return formatter.format(date);
    }

    private static SimpleDateFormat getSimpleFormatter(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }
}
