package in.skdv.skdvinbackend.config;

import com.auth0.client.mgmt.ManagementAPI;
import in.skdv.skdvinbackend.repository.*;
import in.skdv.skdvinbackend.repository.impl.SequenceRepository;
import in.skdv.skdvinbackend.service.*;
import in.skdv.skdvinbackend.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.time.ZoneId;
import java.util.Locale;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Value("${skdvin.cors.enabled:false}")
    private boolean corsEnabled;

    @Value("${skdvin.baseurl:}")
    private String corsUrl;

    @Value("${skdvin.timezone}")
    private String timezone;

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @Bean
    public ZoneId zoneId() {
        return ZoneId.of(timezone);
    }

    @Bean
    @Autowired
    public ISequenceRepository getSequenceService(MongoOperations mongoOperations) {
        return new SequenceRepository(mongoOperations);
    }

    @Bean
    @Autowired
    public IAppointmentService getAppointmentService(ISequenceRepository sequenceService) {
        return new AppointmentService(zoneId(), jumpdayRepository, sequenceService);
    }

    @Bean
    @Autowired
    public IVideoflyerService getVideoflyerService(VideoflyerRepository videoflyerRepository, ISettingsService settingsService) {
        return new VideoflyerService(jumpdayRepository, videoflyerRepository, settingsService);
    }

    @Bean
    public IJumpdayService getJumpdayService() {
        return new JumpdayService(jumpdayRepository);
    }

    @Bean
    @Autowired
    public ITandemmasterService getTandemmasterService(TandemmasterRepository tandemmasterRepository, ISettingsService settingsService) {
        return new TandemmasterService(jumpdayRepository, tandemmasterRepository, settingsService);
    }

    @Bean
    @Autowired
    public ISettingsService getSettingsService(SettingsRepository settingsRepository) {
        return new SettingsService(settingsRepository);
    }

    @Bean
    @Autowired
    public IUserService getUserService(ManagementAPI managementAPI) {
        return new Auth0UserService(managementAPI);
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.GERMANY);
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("ISO-8859-1");
        return messageSource;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (corsEnabled) {
            registry.addMapping("/**")
                    .allowedOrigins(corsUrl)
                    .allowedMethods(
                    HttpMethod.GET.name(),
                    HttpMethod.POST.name(),
                    HttpMethod.PUT.name(),
                    HttpMethod.PATCH.name(),
                    HttpMethod.DELETE.name()
            );
        }
    }
}
