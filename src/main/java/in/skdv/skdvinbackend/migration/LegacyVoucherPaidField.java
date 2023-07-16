package in.skdv.skdvinbackend.migration;

import in.skdv.skdvinbackend.model.entity.voucher.legacy.LegacyVoucherDocument;
import in.skdv.skdvinbackend.repository.LegacyVoucherRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * This migration creates LegacyVoucher paid field
 */
@Slf4j
@ChangeUnit(id = "legacy-voucher-paid-field", order = "5")
@AllArgsConstructor
public class LegacyVoucherPaidField {

    private final LegacyVoucherRepository legacyVoucherRepository;

    @Execution
    public void changeSet() {
        List<LegacyVoucherDocument> vouchers = legacyVoucherRepository.findAll();

        vouchers.forEach(voucher -> {
            if ("keine Zahlungsbestätigung".equals(voucher.getTransactionId())) {
                voucher.setPaid(false);
            } else {
                voucher.setPaid(true);
            }
            legacyVoucherRepository.save(voucher);
        });
    }

    @RollbackExecution
    public void rollback() {
        // nothing to do here
    }
}
