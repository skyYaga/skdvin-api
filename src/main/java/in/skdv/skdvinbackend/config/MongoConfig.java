package in.skdv.skdvinbackend.config;

import in.skdv.skdvinbackend.repository.converter.LocalDateReadConverter;
import in.skdv.skdvinbackend.repository.converter.LocalDateWriteConverter;
import in.skdv.skdvinbackend.repository.converter.LocalTimeReadConverter;
import in.skdv.skdvinbackend.repository.converter.LocalTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                Arrays.asList(
                        new LocalDateWriteConverter(),
                        new LocalDateReadConverter(),
                        new LocalTimeWriteConverter(),
                        new LocalTimeReadConverter()
                )
        );
    }

}
