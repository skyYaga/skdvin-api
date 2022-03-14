package in.skdv.skdvinbackend.repository.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

class LocalTimeReadConverterTest {

    @Test
    void convert() {
        LocalTimeReadConverter converter = new LocalTimeReadConverter();
        String dateString = "09:30";

        LocalTime convertedLocalTime = converter.convert(dateString);

        Assertions.assertEquals(LocalTime.of(9, 30), convertedLocalTime);
    }
}