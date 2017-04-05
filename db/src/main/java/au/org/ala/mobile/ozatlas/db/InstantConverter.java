package au.org.ala.mobile.ozatlas.db;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Instant;

import java.util.Date;

public class InstantConverter implements PropertyConverter<Instant, Date> {
    @Override
    public Instant convertToEntityProperty(Date databaseValue) {
        return Instant.ofEpochMilli(databaseValue.getTime());
    }

    @Override
    public Date convertToDatabaseValue(Instant entityProperty) {
        return DateTimeUtils.toDate(entityProperty);
    }
}
