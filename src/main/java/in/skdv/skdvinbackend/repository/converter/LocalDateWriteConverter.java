package in.skdv.skdvinbackend.repository.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateWriteConverter implements Converter<LocalDate, String> {
    @Override
    public String convert(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
