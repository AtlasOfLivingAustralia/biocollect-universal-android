package au.csiro.ozatlas.manager;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by sad038 on 15/8/17.
 */
public class AtlasDateTimeUtilsTest {
    private final String dateString1 = "2017-04-18T15:07:40Z";
    private final String dateString2 = "2016-02-28T03:03:20Z";

    private final String dateString3 = "2016-03-16 09:45:00";
    private final String dateString4 = "2000-04-10 13:45:00";

    private Calendar calendar;

    @Before
    public void init() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        calendar.set(Calendar.HOUR, 10);
        calendar.set(Calendar.MINUTE, 49);
        calendar.set(Calendar.SECOND, 30);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    @Test
    public void getFormattedDayTime() throws Exception {
        String newFormat = "dd MMM, yyyy";
        assertEquals("18 Apr, 2017", AtlasDateTimeUtils.getFormattedDayTime(dateString1, newFormat));
        assertEquals("28 Feb, 2016", AtlasDateTimeUtils.getFormattedDayTime(dateString2, newFormat));
        assertNotEquals("27 Feb, 2016", AtlasDateTimeUtils.getFormattedDayTime(dateString2, newFormat));
        assertNotEquals("18 Apr, 2016", AtlasDateTimeUtils.getFormattedDayTime(dateString1, newFormat));
    }

    @Test
    public void getFormattedDayTime1() throws Exception {
        String newFormat = "dd MMM, yyyy";
        String oldFormat = "yyyy-MM-dd HH:mm:ss";
        assertEquals("16 Mar, 2016", AtlasDateTimeUtils.getFormattedDayTime(dateString3, oldFormat, newFormat));
        assertEquals("10 Apr, 2000", AtlasDateTimeUtils.getFormattedDayTime(dateString4, oldFormat, newFormat));
    }

    @Test
    public void getDateFromString() throws Exception {
        Date date = calendar.getTime();
        assertEquals(date, AtlasDateTimeUtils.getDateFromString("2017-06-30 10:49:30", "yyyy-MM-dd HH:mm:ss"));
        assertNotEquals(date, AtlasDateTimeUtils.getDateFromString("2017-05-30 10:49:30", "yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    public void getStringFromDate() throws Exception {
        assertEquals("2017-06-30 10:49:30", AtlasDateTimeUtils.getStringFromDate(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
        assertNotEquals("2017-05-30 10:49:30", AtlasDateTimeUtils.getStringFromDate(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
    }

}