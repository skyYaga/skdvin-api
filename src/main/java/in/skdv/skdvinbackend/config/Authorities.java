package in.skdv.skdvinbackend.config;

/**
 * Authorities used in {@link SecurityConfig} to secure the API Endpoints
 */
public final class Authorities {
    public static final String READ_JUMPDAYS = "read:jumpdays";
    public static final String CREATE_JUMPDAYS = "create:jumpdays";
    
    public static final String READ_APPOINTMENTS = "read:appointments";
    public static final String UPDATE_APPOINTMENTS = "update:appointments";
}
