package in.skdv.skdvinbackend.exception;

/**
 * Enum containing error message codes
 */
public enum ErrorMessage {

    APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS("appointment.more.video.than.tandem"),
    APPOINTMENT_SERVICE_ERROR_MSG("appointment.service.error"),

    JUMPDAY_SERVICE_ERROR_MSG("jumpday.service.error"),
    JUMPDAY_NOT_FOUND_MSG("jumpday.not.found"),
    JUMPDAY_ALREADY_EXISTS_MSG("jumpday.already.exists"),
    JUMPDAY_NO_FREE_SLOTS("jumpday.no.free.slots"),

    USER_RESET_PASSWORD_FAILED("user.resetPassword.failed"),
    USER_TOKEN_EXPIRED("user.token.expired"),
    USER_TOKEN_NOT_FOUND("user.token.notfound");

    private final String message;

    /**
     * @param message error message
     */
    ErrorMessage(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
