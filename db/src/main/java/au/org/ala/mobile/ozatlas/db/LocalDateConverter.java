package au.org.ala.mobile.ozatlas.db;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class LocalDateConverter implements PropertyConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseValue(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public LocalDate convertToEntityProperty(String date) {
        if (date == null) return null;
        return LocalDate.parse(date);
    }
}