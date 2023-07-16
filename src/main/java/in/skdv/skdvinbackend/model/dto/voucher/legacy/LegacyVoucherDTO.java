package in.skdv.skdvinbackend.model.dto.voucher.legacy;

import lombok.Value;

@Value
public class LegacyVoucherDTO {
    String id;
    String voucherId;
    boolean redeemed;
    boolean paid;
    String productDescription;
    String value;
    String tax;
    String paymentProvider;
    String transactionId;
    String buyDate;
    String validUntil;
    String redeemDate;
    String salutation;
    String firstName;
    String lastName;
    String street;
    String zipCode;
    String city;
    String phone;
    String email;
}
