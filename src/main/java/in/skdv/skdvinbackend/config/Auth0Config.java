package in.skdv.skdvinbackend.config;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;
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

    @Bean
    public Auth0HttpClient auth0HttpClient() {
        return DefaultHttpClient.newBuilder()
                .withConnectTimeout(10)
                .withReadTimeout(10)
                .build();
    }
    @Bean
    public AuthAPI authAPI() {
        return AuthAPI.newBuilder(domain, clientId, clientSecret)
                .withHttpClient(auth0HttpClient())
                .build();
    }

    @Bean
    @Autowired
    public ManagementAPI managementAPI(AuthAPI authAPI) throws Auth0Exception {
        TokenRequest tokenRequest = authAPI.requestToken("https://" + domain + "/api/v2/");
        TokenHolder response = tokenRequest.execute().getBody();
        String accessToken = response.getAccessToken();
        return ManagementAPI.newBuilder(domain, accessToken)
                .withHttpClient(auth0HttpClient())
                .build();
    }
}
