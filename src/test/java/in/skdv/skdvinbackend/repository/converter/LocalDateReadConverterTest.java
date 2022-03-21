package in.skdv.skdvinbackend.repository.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class LocalDateReadConverterTest {

    @Test
    void convert() {
        LocalDateReadConverter converter = new LocalDateReadConverter();
        String dateString = "2022-02-01";

        LocalDate convertedLocalDate = converter.convert(dateString);

        Assertions.assertEquals(LocalDate.of(2022, 2, 1), convertedLocalDate);
    }
}