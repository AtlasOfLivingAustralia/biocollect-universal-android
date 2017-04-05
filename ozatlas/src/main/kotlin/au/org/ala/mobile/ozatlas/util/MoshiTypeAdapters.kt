package au.org.ala.mobile.ozatlas.util

import au.org.ala.mobile.ozatlas.biocollect.dto.EmbargoOption
import com.squareup.moshi.Moshi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.SignStyle
import org.threeten.bp.temporal.ChronoField.*


/**
 * [Moshi] Adapter for (de-)serialising JSR-310 objects
 */
@SuppressWarnings("unused")
class MoshiTypeAdapters {

    companion object {
        @JvmStatic
        val ALA_LOCAL_TIME = DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(CLOCK_HOUR_OF_AMPM, 1, 2, SignStyle.NEVER).appendLiteral(':').appendValue(MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(' ').optionalEnd().appendText(AMPM_OF_DAY).parseLenient().toFormatter()
    }

    @ToJson fun embargoOptionToJson(embargoOption: EmbargoOption?) =
            mapOf("enumType" to "au.org.ala.ecodata.EmbargoOption", "name" to embargoOption?.name)
    @FromJson fun embargoOptionFromJson(map: Map<String, String>?) =
            map?.get("name")?.let { name -> EmbargoOption.valueOf(name) }

    @ToJson fun localTimeToJson(localTime: LocalTime?) =
            localTime?.format(ALA_LOCAL_TIME)
    @FromJson fun localTimeFromJson(value: String?) =
            value?.let { LocalTime.parse(value, ALA_LOCAL_TIME) }

    @ToJson fun localDateToJson(localDate: LocalDate?) =
            localDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    @FromJson fun localDateFromJson(value: String?) =
            value?.let { LocalDate.parse(value) }

    @ToJson fun instantToJson(instant: Instant?) =
            instant?.let { DateTimeFormatter.ISO_INSTANT.format(instant) }
    @FromJson fun instantFromJson(value: String?) =
            value?.let { Instant.parse(value) }
}
