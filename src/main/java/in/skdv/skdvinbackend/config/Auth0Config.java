package in.skdv.skdvinbackend.config;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.net.client.Auth0HttpClient;
import com.auth0.net.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Auth0Config {

    @Value("${auth0.management.domain}")
    private String domain;

    @Value("${auth0.management.client-id}")
    private String clientId;

    @Value("${auth0.management.client-secret}")
    private String clientSecret;

    @Value("${auth0.management.audience}")
    private String audience;

    @Bean
    public Auth0HttpClient auth0HttpClient() {
        return DefaultHttpClient.newBuilder()
                .withConnectTimeout(10)
                .withReadTimeout(10)
                .build();
    }
    @Bean
    @Autowired
    public AuthAPI authAPI(Auth0HttpClient auth0HttpClient) {
        return AuthAPI.newBuilder(domain, clientId, clientSecret)
                .withHttpClient(auth0HttpClient)
                .build();
    }

    @Bean
    @Autowired
    public ManagementAPI managementAPI(Auth0HttpClient auth0HttpClient)  {
        return ManagementAPI.newBuilder(domain, "dummy")
                .withHttpClient(auth0HttpClient)
                .build();
    }
}
