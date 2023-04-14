package in.skdv.skdvinbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
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

    @Bean
    public ZoneId zoneId() {
        return ZoneId.of(timezone);
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
