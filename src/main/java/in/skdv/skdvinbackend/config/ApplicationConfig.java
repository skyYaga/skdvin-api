package in.skdv.skdvinbackend.config;

import com.auth0.client.mgmt.ManagementAPI;
import in.skdv.skdvinbackend.repository.*;
import in.skdv.skdvinbackend.service.*;
import in.skdv.skdvinbackend.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

public class ApplicationConfig implements WebMvcConfigurer {

    @Value("${skdvin.cors.enabled:false}")
    private boolean corsEnabled;

    @Value("${skdvin.baseurl:}")
    private String corsUrl;

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TandemmasterRepository tandemmasterRepository;

    @Autowired
    private VideoflyerRepository videoflyerRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private WaiverRepository waiverRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public ISequenceService getSequenceService() {
        return new SequenceService();
    }

    @Bean
    @Autowired
    public IAppointmentService getAppointmentService(ISequenceService sequenceService) {
        return new MongoAppointmentService(jumpdayRepository, appointmentRepository, sequenceService, mongoTemplate);
    }

    @Bean
    @Autowired
    public IVideoflyerService getVideoflyerService(ISettingsService settingsService) {
        return new MongoVideoflyerService(jumpdayRepository, videoflyerRepository, settingsService);
    }

    @Bean
    public IJumpdayService getJumpdayService() {
        return new MongoJumpdayService(jumpdayRepository);
    }

    @Bean
    @Autowired
    public ITandemmasterService getTandemmasterService(ISettingsService settingsService) {
        return new MongoTandemmasterService(jumpdayRepository, tandemmasterRepository, settingsService);
    }

    @Bean
    public ISettingsService getSettingsService() {
        return new MongoSettingsService(settingsRepository);
    }

    @Bean
    public IWaiverService getWaiverService() {
        return new MongoWaiverService(waiverRepository);
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
        messageSource.setDefaultEncoding("UTF-8");
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
