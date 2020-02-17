package in.skdv.skdvinbackend.exception;

/**
 * Enum containing error message codes
 */
public enum ErrorMessage {

    APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS("appointment.more.video.than.tandem"),
    APPOINTMENT_SERVICE_ERROR_MSG("appointment.service.error"),
    APPOINTMENT_NO_FREE_SLOTS("appointment.no.free.slots"),

    JUMPDAY_SERVICE_ERROR_MSG("jumpday.service.error"),
    JUMPDAY_NOT_FOUND_MSG("jumpday.not.found"),
    JUMPDAY_ALREADY_EXISTS_MSG("jumpday.already.exists"),
    JUMPDAY_NO_FREE_SLOTS("jumpday.no.free.slots");

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
