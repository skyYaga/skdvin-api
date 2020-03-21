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
}
