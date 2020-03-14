package in.skdv.skdvinbackend.exception;

/**
 * Enum containing error message codes
 */
public enum ErrorMessage {

    APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS("appointment.more.video.than.tandem"),
    APPOINTMENT_SERVICE_ERROR_MSG("appointment.service.error"),
    APPOINTMENT_NO_FREE_SLOTS("appointment.no.free.slots"),
    APPOINTMENT_MISSING_JUMPER_INFO("appointment.missing.jumper.info"),
    APPOINTMENT_NOT_FOUND("appointment.not.found"),
    APPOINTMENT_CONFIRMATION_TOKEN_INVALID("appointment.confirmation.token.invalid"),
    APPOINTMENT_ALREADY_CONFIRMED("appointment.already.confirmed"),

    JUMPDAY_SERVICE_ERROR_MSG("jumpday.service.error"),
    JUMPDAY_NOT_FOUND_MSG("jumpday.not.found"),
    JUMPDAY_ALREADY_EXISTS_MSG("jumpday.already.exists"),
    JUMPDAY_NO_FREE_SLOTS("jumpday.no.free.slots"),
    JUMPDAY_INVALID("jumpday.invalid"),
    JUMPDAY_SLOT_HAS_APPOINTMENTS("jumpday.slot.has.apointments"),
    JUMPDAY_HAS_APPOINTMENTS("jumpday.has.apointments");

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
