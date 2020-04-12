package in.skdv.skdvinbackend;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static in.skdv.skdvinbackend.config.Authorities.*;

public class MockJwtDecoder {

    private static final String SIMPLIFIED_CREATE_JUMPDAYS = simplifyPermission(CREATE_JUMPDAYS);
    private static final String SIMPLIFIED_READ_JUMPDAYS = simplifyPermission(READ_JUMPDAYS);
    private static final String SIMPLIFIED_UPDATE_JUMPDAYS = simplifyPermission(UPDATE_JUMPDAYS);

    private static final String SIMPLIFIED_CREATE_APPOINTMENTS = simplifyPermission(CREATE_APPOINTMENTS);
    private static final String SIMPLIFIED_READ_APPOINTMENTS = simplifyPermission(READ_APPOINTMENTS);
    private static final String SIMPLIFIED_UPDATE_APPOINTMENTS = simplifyPermission(UPDATE_APPOINTMENTS);

    private static final String SIMPLIFIED_CREATE_TANDEMMASTER = simplifyPermission(CREATE_TANDEMMASTER);
    private static final String SIMPLIFIED_UPDATE_TANDEMMASTER = simplifyPermission(UPDATE_TANDEMMASTER);
    private static final String SIMPLIFIED_READ_TANDEMMASTER = simplifyPermission(READ_TANDEMMASTER);
    private static final String SIMPLIFIED_DELETE_TANDEMMASTER = simplifyPermission(DELETE_TANDEMMASTER);

    private static final String SIMPLIFIED_CREATE_VIDEOFLYER = simplifyPermission(CREATE_VIDEOFLYER);
    private static final String SIMPLIFIED_UPDATE_VIDEOFLYER = simplifyPermission(UPDATE_VIDEOFLYER);
    private static final String SIMPLIFIED_READ_VIDEOFLYER = simplifyPermission(READ_VIDEOFLYER);
    private static final String SIMPLIFIED_DELETE_VIDEOFLYER = simplifyPermission(DELETE_VIDEOFLYER);

    private static final String SIMPLIFIED_CREATE_SETTINGS = simplifyPermission(CREATE_SETTINGS);
    private static final String SIMPLIFIED_READ_SETTINGS = simplifyPermission(READ_SETTINGS);
    private static final String SIMPLIFIED_UPDATE_SETTINGS = simplifyPermission(UPDATE_SETTINGS);

    public static Jwt decode(String permission) throws JwtException {
        return new Jwt(permission,
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.MINUTES),
                Collections.singletonMap("foo", "bar"),
                Collections.singletonMap("permissions", convertPermission(permission)));
    }

    private static String convertPermission(String permission) {
        String convertedPermission = "";
        if (SIMPLIFIED_CREATE_JUMPDAYS.equals(permission)) {
            convertedPermission = CREATE_JUMPDAYS;
        }
        if (SIMPLIFIED_READ_JUMPDAYS.equals(permission)) {
            convertedPermission = READ_JUMPDAYS;
        }
        if (SIMPLIFIED_UPDATE_JUMPDAYS.equals(permission)) {
            convertedPermission = UPDATE_JUMPDAYS;
        }
        if (SIMPLIFIED_CREATE_APPOINTMENTS.equals(permission)) {
            convertedPermission = CREATE_APPOINTMENTS;
        }
        if (SIMPLIFIED_READ_APPOINTMENTS.equals(permission)) {
            convertedPermission = READ_APPOINTMENTS;
        }
        if (SIMPLIFIED_UPDATE_APPOINTMENTS.equals(permission)) {
            convertedPermission = UPDATE_APPOINTMENTS;
        }
        if (SIMPLIFIED_CREATE_TANDEMMASTER.equals(permission)) {
            convertedPermission = CREATE_TANDEMMASTER;
        }
        if (SIMPLIFIED_UPDATE_TANDEMMASTER.equals(permission)) {
            convertedPermission = UPDATE_TANDEMMASTER;
        }
        if (SIMPLIFIED_READ_TANDEMMASTER.equals(permission)) {
            convertedPermission = READ_TANDEMMASTER;
        }
        if (SIMPLIFIED_DELETE_TANDEMMASTER.equals(permission)) {
            convertedPermission = DELETE_TANDEMMASTER;
        }
        if (SIMPLIFIED_CREATE_VIDEOFLYER.equals(permission)) {
            convertedPermission = CREATE_VIDEOFLYER;
        }
        if (SIMPLIFIED_UPDATE_VIDEOFLYER.equals(permission)) {
            convertedPermission = UPDATE_VIDEOFLYER;
        }
        if (SIMPLIFIED_READ_VIDEOFLYER.equals(permission)) {
            convertedPermission = READ_VIDEOFLYER;
        }
        if (SIMPLIFIED_DELETE_VIDEOFLYER.equals(permission)) {
            convertedPermission = DELETE_VIDEOFLYER;
        }
        if (SIMPLIFIED_CREATE_SETTINGS.equals(permission)) {
            convertedPermission = CREATE_SETTINGS;
        }
        if (SIMPLIFIED_READ_SETTINGS.equals(permission)) {
            convertedPermission = READ_SETTINGS;
        }
        if (SIMPLIFIED_UPDATE_SETTINGS.equals(permission)) {
            convertedPermission = UPDATE_SETTINGS;
        }

        return convertedPermission.replace("SCOPE_", "");
    }

    public static String addHeader(String permission) {
        return "Bearer " + simplifyPermission(permission);
    }

    private static String simplifyPermission(String permission) {
        return permission.replace("SCOPE_", "").replace(":", "");
    }

}
