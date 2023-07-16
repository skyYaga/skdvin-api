package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.exception.VoucherAlreadyRedeemedException;
import in.skdv.skdvinbackend.model.domain.LegacyVoucher;
import in.skdv.skdvinbackend.model.entity.voucher.legacy.LegacyVoucherDocument;
import in.skdv.skdvinbackend.model.mapper.LegacyVoucherMapper;
import in.skdv.skdvinbackend.repository.LegacyVoucherRepository;
import in.skdv.skdvinbackend.service.ILegacyVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyVoucherService implements ILegacyVoucherService {

    private final LegacyVoucherRepository legacyVoucherRepository;
    private final LegacyVoucherMapper legacyVoucherMapper;

    @Override
    public LegacyVoucher findVoucher(String id) {
        Optional<LegacyVoucherDocument> legacyVoucherDocument = legacyVoucherRepository.findById(id);
        if (legacyVoucherDocument.isEmpty()) {
            log.warn("Voucher with id {} not found.", id);
            throw new NotFoundException(ErrorMessage.VOUCHER_NOT_FOUND);
        }

        return legacyVoucherMapper.toDomain(legacyVoucherDocument.get());
    }

    @Override
    public LegacyVoucher redeemVoucher(String id) {
        LegacyVoucher voucher = findVoucher(id);
        if (voucher.isRedeemed()) {
            log.error("Voucher with id {} was already redeemed on {}.", id, voucher.getRedeemDate());
            throw new VoucherAlreadyRedeemedException(ErrorMessage.VOUCHER_ALREADY_REDEEMED);
        }

        LegacyVoucherDocument voucherDocument = legacyVoucherMapper.toDocument(voucher);
        voucherDocument.setRedeemed(true);
        voucherDocument.setRedeemDate(Instant.now().toString());

        return legacyVoucherMapper.toDomain(legacyVoucherRepository.save(voucherDocument));
    }
}
