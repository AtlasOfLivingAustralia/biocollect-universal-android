package au.org.ala.mobile.ozatlas.greendao

import org.greenrobot.greendao.converter.PropertyConverter
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class LocalTimeConverter : PropertyConverter<LocalTime, String> {

    override fun convertToDatabaseValue(localTime: LocalTime?): String? = localTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)

    override fun convertToEntityProperty(time: String?): LocalTime? = time?.let { LocalTime.parse(time) }
}