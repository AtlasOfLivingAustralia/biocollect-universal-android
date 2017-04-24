package au.csiro.ozatlas.manager;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by najmussadat on 1/09/2015.
 */
public class AtlasDateTimeUtils {
    private final static String TAG = "AtlasDateTimeUtils";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //2017-04-18T15:07:40Z
    public static final String DEFAULT_TIME_FORMAT = "hh:mm a";

    public static TimeZone deviceTimeZone;
    public static SimpleDateFormat serverDateTimeFormat;

    public static TimeZone getDeviceTimeZone() {
        if (deviceTimeZone == null) {
            deviceTimeZone = TimeZone.getDefault();
        }
        return deviceTimeZone;
    }

    public static String getDeviceTimeZoneString() {
        return getDeviceTimeZone().getID();
    }

    public static void setServerDateTimeFormat(SimpleDateFormat serverDateTimeFormat) {
        AtlasDateTimeUtils.serverDateTimeFormat = serverDateTimeFormat;
    }

    public static SimpleDateFormat getServerDateTimeFormat() {
        if (serverDateTimeFormat == null) {
            serverDateTimeFormat = getSimpleFormatter(DEFAULT_DATE_FORMAT);
            //serverDateTimeFormat.setTimeZone(TimeZone.getTimeZone(SERVER_TIMEZONE));
        }
        return serverDateTimeFormat;
    }

    public static String getCurrentTime(String format) {
        return getSimpleFormatter(format).format(Calendar.getInstance().getTime());
    }

    public static String getCurrentTime() {
        return getCurrentTime(DEFAULT_DATE_FORMAT);
    }

    public static String getFormattedDayTime(String dateString, String senderFormat, String expectedFormat) {
        SimpleDateFormat sdf = getSimpleFormatter(senderFormat);
        try {
            return getSimpleFormatter(expectedFormat).format(sdf.parse(dateString));
        } catch (ParseException p) {
            Log.d(TAG, p.getMessage());
        }
        return "";
    }

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

    public static String getFormattedDayTime(String dateString, String format) {
        return getFormattedDayTime(dateString, DEFAULT_DATE_FORMAT, format);
    }

    public static boolean isOlderThanNow(String time) {
        if (time == null) {
            return false;
        }
        SimpleDateFormat sdf = getServerDateTimeFormat();
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date inputDate = sdf.parse(time);
            Date today = new Date();
            return inputDate.before(today);
        } catch (ParseException p) {
            Log.d(TAG, p.getMessage());
        }
        return true;
    }

    public static boolean isNewerThanFollowingDate(String date1, String date2) {
        if (date1 != null && date2 != null) {
            SimpleDateFormat sdf = getServerDateTimeFormat();
            try {
                Date inputDate1 = sdf.parse(date1);
                Date inputDate2 = sdf.parse(date2);
                return inputDate2.after(inputDate1);
            } catch (ParseException p) {
                Log.d(TAG, p.getMessage());
            }
        }
        return false;
    }

    public static String addDaysToDate(String startDate, int numberOfDaysToAdd, String format) {
        if (format == null) {
            format = DEFAULT_DATE_FORMAT;
        }
        SimpleDateFormat sdf = getSimpleFormatter(format);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, numberOfDaysToAdd);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = getSimpleFormatter(format);
        return sdf1.format(c.getTime());
    }

    public static String addHoursToDate(String startDate, int numberOfHoursToAdd, String format) {
        if (format == null) {
            format = DEFAULT_DATE_FORMAT;
        }
        SimpleDateFormat sdf = getSimpleFormatter(format);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.HOUR, numberOfHoursToAdd);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = getSimpleFormatter(format);
        return sdf1.format(c.getTime());
    }

    public static Date addDaysToDate(Date startDate, int numberOfDaysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDaysToAdd);
        return calendar.getTime();
    }


    public static Date getDateFromString(String date, String format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());

            return formatter.parse(date);
        } catch (Exception ex) {
            Log.d(TAG, "Errors in getDateFromString:" + ex);
            return null;
        }
    }

    public static Date getDateFromString(String date) {
        return getDateFromString(date, DEFAULT_DATE_FORMAT);
    }

    public static String getStringFromDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = getSimpleFormatter(format);
        return formatter.format(date);
    }

    public static String getStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = getSimpleFormatter(DEFAULT_DATE_FORMAT);
        return formatter.format(date);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isFollowingDayTomorrow(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR) == 1;
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static SimpleDateFormat getSimpleFormatter(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private static Calendar get12AM(Date inputDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputDate);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    //convert 3:12 pm to 15:12:00
    public static String get24HourString(String timeString) {
        try {
            SimpleDateFormat sdf = getSimpleFormatter(DEFAULT_TIME_FORMAT);
            final Date dateObj = sdf.parse(timeString);
            String dateString = getSimpleFormatter("HH:mm").format(dateObj);
            dateString = dateString + ":00";
            return dateString;
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convert24HrTo12Hr(String _24HourTime) {
        try {
            SimpleDateFormat _24HourSDF = getSimpleFormatter("HH:mm");
            SimpleDateFormat _12HourSDF = getSimpleFormatter("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            //System.out.println(_24HourDt);
            return _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
