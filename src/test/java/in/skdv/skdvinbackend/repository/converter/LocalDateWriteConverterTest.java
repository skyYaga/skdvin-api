package in.skdv.skdvinbackend.repository.converter;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalDateWriteConverterTest {

    @Test
    void convert() {
        LocalDateWriteConverter converter = new LocalDateWriteConverter();
        LocalDate localDate = LocalDate.of(2022, 2, 1);

        String convertedDate = converter.convert(localDate);

        assertEquals("2022-02-01", convertedDate);
    }
}