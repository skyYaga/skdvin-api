package in.skdv.skdvinbackend.repository.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

public class LocalTimeWriteConverter implements Converter<LocalTime, String> {

    public static final DateTimeFormatter HH_MM = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .toFormatter();

    @Override
    public String convert(LocalTime localTime) {
        return localTime.format(HH_MM);
    }
}
