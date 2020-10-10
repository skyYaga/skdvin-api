package in.skdv.skdvinbackend.config;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.AuthRequest;
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
    public ManagementAPI managementAPI() throws Auth0Exception {
        AuthAPI authAPI = new AuthAPI(domain, clientId, clientSecret);
        AuthRequest authRequest = authAPI.requestToken("https://" + domain + "/api/v2/");
        TokenHolder holder = authRequest.execute();
        return new ManagementAPI(domain, holder.getAccessToken());
    }
}
