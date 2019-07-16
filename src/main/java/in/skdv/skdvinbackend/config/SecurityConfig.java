package in.skdv.skdvinbackend.config;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static in.skdv.skdvinbackend.config.Authorities.*;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${auth0.apiAudience}")
    private String apiAudience;

    @Value("${auth0.issuer}")
    private String issuer;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        // Auth0 config
        JwtWebSecurityConfigurer
                .forRS256(apiAudience, issuer)
                .configure(http)
                .authorizeRequests()
                .antMatchers("/docs/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/jumpday").hasAuthority(READ_JUMPDAYS)
                .antMatchers(HttpMethod.POST, "/api/jumpday").hasAuthority(CREATE_JUMPDAYS)
                .antMatchers(HttpMethod.GET, "/api/jumpday/{jumpdayDate}").hasAuthority(READ_JUMPDAYS)
                .antMatchers(HttpMethod.GET, "/api/appointment/{appointmentId}").hasAuthority(READ_APPOINTMENTS)
                .antMatchers(HttpMethod.POST, "/api/appointment/{appointmentId}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/appointment/{appointmentId}").hasAuthority(UPDATE_APPOINTMENTS)
                .anyRequest().authenticated()
                // Disable Session Management as it's a REST API
                .and().sessionManagement().disable();
    }

}
