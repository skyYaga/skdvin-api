package in.skdv.skdvinbackend.config;

import in.skdv.skdvinbackend.listener.JumpdayCascadeSaveMongoEventListener;
import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISequenceService;
import in.skdv.skdvinbackend.service.impl.MongoAppointmentService;
import in.skdv.skdvinbackend.service.impl.MongoJumpdayService;
import in.skdv.skdvinbackend.service.impl.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

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
    private MongoTemplate mongoTemplate;

    @Bean
    private JumpdayCascadeSaveMongoEventListener jumpdayCascadeSaveMongoEventListener() {
        return new JumpdayCascadeSaveMongoEventListener(mongoTemplate);
    }

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
    public IJumpdayService getJumpdayService() {
        return new MongoJumpdayService(jumpdayRepository);
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.GERMANY);
        return resolver;
    }

    @Bean
    private LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
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
                    HttpMethod.PATCH.name()
            );
        }
    }
}
