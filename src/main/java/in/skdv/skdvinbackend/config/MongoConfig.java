package in.skdv.skdvinbackend.config;

import in.skdv.skdvinbackend.repository.converter.LocalDateReadConverter;
import in.skdv.skdvinbackend.repository.converter.LocalDateWriteConverter;
import in.skdv.skdvinbackend.repository.converter.LocalTimeReadConverter;
import in.skdv.skdvinbackend.repository.converter.LocalTimeWriteConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${skdvin.dbname}")
    private String dbName;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter converterConfigurationAdapter) {
        super.configureConverters(converterConfigurationAdapter);
        converterConfigurationAdapter.registerConverter(new LocalDateWriteConverter());
        converterConfigurationAdapter.registerConverter(new LocalDateReadConverter());
        converterConfigurationAdapter.registerConverter(new LocalTimeWriteConverter());
        converterConfigurationAdapter.registerConverter(new LocalTimeReadConverter());
    }
}
