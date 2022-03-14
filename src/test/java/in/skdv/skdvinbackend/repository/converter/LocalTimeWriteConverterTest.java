package in.skdv.skdvinbackend.repository.converter;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalTimeWriteConverterTest {

    @Test
    void convert() {
        LocalTimeWriteConverter converter = new LocalTimeWriteConverter();
        LocalTime localTime = LocalTime.of(9,  30);

        String convertedTime = converter.convert(localTime);

        assertEquals("09:30", convertedTime);
    }
}