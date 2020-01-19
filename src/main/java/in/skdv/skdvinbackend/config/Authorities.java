package in.skdv.skdvinbackend.config;

/**
 * Authorities used in {@link SecurityConfig} to secure the API Endpoints
 */
public final class Authorities {
    public static final String READ_JUMPDAYS = "SCOPE_read:jumpdays";
    public static final String CREATE_JUMPDAYS = "SCOPE_create:jumpdays";
    
    public static final String READ_APPOINTMENTS = "SCOPE_read:appointments";
    public static final String UPDATE_APPOINTMENTS = "SCOPE_update:appointments";
}
