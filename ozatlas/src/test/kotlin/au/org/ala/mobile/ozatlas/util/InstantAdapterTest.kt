package au.org.ala.mobile.ozatlas.util

import au.org.ala.mobile.ozatlas.biocollect.dto.EmbargoOption
import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.*

class InstantAdapterTest {

    @Test fun testToLocalTime() {
        assertEquals("2:03 AM", MoshiTypeAdapters().localTimeToJson(LocalTime.of(2,3,4,5)))
    }

    @Test fun testFromLocalTime() {
        assertEquals(LocalTime.NOON, MoshiTypeAdapters().localTimeFromJson("12:00 PM"))
    }

    @Test fun testToLocalDate() {
        assertEquals("2016-07-23", MoshiTypeAdapters().localDateToJson(LocalDate.of(2016, Month.JULY, 23)))
    }

    @Test fun testFromLocalDate() {
        assertEquals(LocalDate.of(2016, Month.JULY, 23), MoshiTypeAdapters().localDateFromJson("2016-07-23"))
    }

    @Test fun testFromInstant() {
        assertEquals("1970-01-01T00:00:00Z", MoshiTypeAdapters().instantToJson(Instant.ofEpochMilli(0)))
    }

    @Test fun testToInstant() {
        assertEquals(Instant.ofEpochMilli(0), MoshiTypeAdapters().instantFromJson("1970-01-01T00:00:00Z"))
    }

    @Test fun testToMapFromEmbargoStatus() {
        assertEquals(mapOf("enumType" to "au.org.ala.ecodata.EmbargoOption", "name" to "NONE"), MoshiTypeAdapters().embargoOptionToJson(EmbargoOption.NONE))
    }

    @Test fun testFromMapToEmbargoStatus() {
        assertEquals(EmbargoOption.NONE, MoshiTypeAdapters().embargoOptionFromJson(mapOf("enumType" to "au.org.ala.ecodata.EmbargoOption", "name" to "NONE")))
    }
}