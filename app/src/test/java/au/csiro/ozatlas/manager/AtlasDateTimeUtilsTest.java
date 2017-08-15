package au.csiro.ozatlas.manager;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sad038 on 15/8/17.
 */
public class AtlasDateTimeUtilsTest {
    String dateString1 = "2017-04-18T15:07:40Z";
    String dateString2 = "2016-02-28T03:03:20Z";

    String dateString3 = "2016-03-16 09:45:00";
    String dateString4 = "2000-04-10 13:45:00";

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
    public void getFormattedDayTime2() throws Exception {

    }

    @Test
    public void getDateFromString() throws Exception {

    }

    @Test
    public void getDateFromString1() throws Exception {

    }

    @Test
    public void getStringFromDate() throws Exception {

    }

    @Test
    public void getStringFromDate1() throws Exception {

    }

}