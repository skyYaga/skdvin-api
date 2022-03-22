package in.skdv.skdvinbackend.repository.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;

public class LocalDateReadConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String localDateString) {
        return LocalDate.parse(localDateString);
    }
}
