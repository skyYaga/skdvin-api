package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.domain.LegacyVoucher;

public interface ILegacyVoucherService {
    LegacyVoucher findVoucher(String id);

    LegacyVoucher redeemVoucher(String id);
}
