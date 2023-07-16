package in.skdv.skdvinbackend.model.entity.voucher.legacy;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("legacyVouchers")
@Data
public class LegacyVoucherDocument {

    @Id
    private String id;
    private String voucherId;
    private boolean redeemed;
    private boolean paid;
    private String productDescription;
    private String value;
    private String tax;
    private String paymentProvider;
    private String transactionId;
    private String buyDate;
    private String validUntil;
    private String redeemDate;
    private String salutation;
    private String firstName;
    private String lastName;
    private String street;
    private String zipCode;
    private String city;
    private String phone;
    private String email;
}
