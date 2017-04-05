package au.org.ala.mobile.ozatlas.greendao

import org.greenrobot.greendao.converter.PropertyConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LocalDateConverter : PropertyConverter<LocalDate, String> {

    override fun convertToDatabaseValue(localDate: LocalDate?): String? = localDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)

    override fun convertToEntityProperty(date: String?): LocalDate? = date?.let { LocalDate.parse(date) }
}