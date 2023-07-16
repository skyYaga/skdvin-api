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
 * This migration migrates LegacyVoucher redeem fields
 */
@Slf4j
@ChangeUnit(id = "legacy-voucher-migration", order = "4")
@AllArgsConstructor
public class LegacyVoucherMigration {

    private final LegacyVoucherRepository legacyVoucherRepository;

    @Execution
    public void changeSet() {
        List<LegacyVoucherDocument> vouchers = legacyVoucherRepository.findAll();

        vouchers.forEach(voucher -> {
            if ("nicht eingelöst".equals(voucher.getRedeemDate())) {
                voucher.setRedeemDate(null);
                voucher.setRedeemed(false);
            } else {
                voucher.setRedeemed(true);
            }
            legacyVoucherRepository.save(voucher);
        });
    }

    @RollbackExecution
    public void rollback() {
        // nothing to do here
    }
}
