package au.org.ala.mobile.ozatlas.db;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.SignStyle;

import static org.threeten.bp.temporal.ChronoField.AMPM_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.CLOCK_HOUR_OF_AMPM;
import static org.threeten.bp.temporal.ChronoField.MINUTE_OF_HOUR;

class LocalTimeConverter implements PropertyConverter<LocalTime, String> {

    private static DateTimeFormatter ALA_LOCAL_TIME =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .parseLenient()
                    .appendValue(CLOCK_HOUR_OF_AMPM, 1, 2, SignStyle.NEVER)
                    .appendLiteral(':')
                    .appendValue(MINUTE_OF_HOUR, 2)
                    .optionalStart()
                    .appendLiteral(' ')
                    .optionalEnd()
                    .appendText(AMPM_OF_DAY)
                    .toFormatter();

    @Override
    public String convertToDatabaseValue(LocalTime localTime) {
        if (localTime == null) return null;
        return localTime.format(ALA_LOCAL_TIME);
    }

    @Override
    public LocalTime convertToEntityProperty(String time) {
        if (time == null) return null;
        return LocalTime.parse(time, ALA_LOCAL_TIME);
    }
}