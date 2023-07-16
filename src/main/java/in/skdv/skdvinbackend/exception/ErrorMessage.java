package in.skdv.skdvinbackend.exception;

/**
 * Enum containing error message codes
 */
public enum ErrorMessage {

    INTERNAL_SERVICE_EXCEPTION("internal.service.exception"),

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
    JUMPDAY_INVALID_MORE_VIDEO_THAN_TANDEM("jumpday.invalid.more.video.than.tandem"),
    JUMPDAY_INVALID_MORE_PICANDVID_THAN_PICORVID("jumpday.invalid.more.picandvid.than.picorvid"),
    JUMPDAY_SLOT_HAS_APPOINTMENTS("jumpday.slot.has.apointments"),
    JUMPDAY_HAS_APPOINTMENTS("jumpday.has.apointments"),

    TANDEMMASTER_NOT_FOUND("tandemmaster.not.found"),

    VIDEOFLYER_NOT_FOUND("videoflyer.not.found"),

    SETTINGS_NOT_FOUND("settings.not.found"),

    SELFASSIGNMENT_INVALID("selfassignment.invalid"),
    SELFASSIGNMENT_NODELETE("selfassignment.nodelete"),
    SELFASSIGNMENT_READONLY("selfassignment.readonly"),

    VOUCHER_ALREADY_REDEEMED("voucher.already.redeemed"),
    VOUCHER_NOT_FOUND("voucher.not.found");

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
