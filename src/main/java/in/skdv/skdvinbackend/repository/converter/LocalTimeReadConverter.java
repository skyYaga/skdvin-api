package in.skdv.skdvinbackend.repository.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

public class LocalTimeReadConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String localTimeString) {
        return LocalTime.parse(localTimeString);
    }
}
