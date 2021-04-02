package in.skdv.skdvinbackend.config;

/**
 * Authorities used in {@link SecurityConfig} to secure the API Endpoints
 */
public final class Authorities {

    private Authorities() {
        throw new IllegalStateException("Utility class that should not be instantiated.");
    }

    public static final String READ_JUMPDAYS = "SCOPE_read:jumpdays";
    public static final String CREATE_JUMPDAYS = "SCOPE_create:jumpdays";
    public static final String UPDATE_JUMPDAYS = "SCOPE_update:jumpdays";

    public static final String CREATE_APPOINTMENTS = "SCOPE_create:appointments";
    public static final String READ_APPOINTMENTS = "SCOPE_read:appointments";
    public static final String UPDATE_APPOINTMENTS = "SCOPE_update:appointments";

    public static final String CREATE_TANDEMMASTER = "SCOPE_create:tandemmaster";
    public static final String UPDATE_TANDEMMASTER = "SCOPE_update:tandemmaster";
    public static final String READ_TANDEMMASTER = "SCOPE_read:tandemmaster";
    public static final String DELETE_TANDEMMASTER = "SCOPE_delete:tandemmaster";

    public static final String CREATE_VIDEOFLYER = "SCOPE_create:videoflyer";
    public static final String UPDATE_VIDEOFLYER = "SCOPE_update:videoflyer";
    public static final String READ_VIDEOFLYER = "SCOPE_read:videoflyer";
    public static final String DELETE_VIDEOFLYER = "SCOPE_delete:videoflyer";

    public static final String CREATE_SETTINGS = "SCOPE_create:settings";
    public static final String READ_SETTINGS = "SCOPE_read:settings";
    public static final String UPDATE_SETTINGS = "SCOPE_update:settings";

    public static final String TANDEMMASTER = "SCOPE_tandemmaster";
    public static final String VIDEOFLYER = "SCOPE_videoflyer";

    public static final String READ_USERS = "SCOPE_read:users";
    public static final String UPDATE_USERS = "SCOPE_update:users";

    public static final String READ_WAIVERS = "SCOPE_read:waivers";
    public static final String UPDATE_WAIVERS = "SCOPE_update:waivers";
}
