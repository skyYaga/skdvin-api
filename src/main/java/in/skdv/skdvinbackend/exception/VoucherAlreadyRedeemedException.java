package in.skdv.skdvinbackend.exception;

public class VoucherAlreadyRedeemedException extends ConflictException {

    public VoucherAlreadyRedeemedException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
