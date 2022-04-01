package in.skdv.skdvinbackend.task;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.AuthRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateAuth0TokenTask {

    @Value("${auth0.management.domain}")
    private String domain;

    private final ManagementAPI managementAPI;
    private final AuthAPI authAPI;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 8, initialDelay = 1000 * 60) // every 8 hours
    public void updateToken() {
        AuthRequest authRequest = authAPI.requestToken("https://" + domain + "/api/v2/");
        try {
            TokenHolder holder = authRequest.execute();
            managementAPI.setApiToken(holder.getAccessToken());
        } catch (Auth0Exception e) {
            log.error("Error updating Auth0 token", e);
        }
    }
}
