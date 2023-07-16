package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.model.domain.LegacyVoucher;
import in.skdv.skdvinbackend.model.dto.voucher.legacy.LegacyVoucherDTO;
import in.skdv.skdvinbackend.model.entity.voucher.legacy.LegacyVoucherDocument;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface LegacyVoucherMapper {
    LegacyVoucherDTO toDto(LegacyVoucher legacyVoucher);
    LegacyVoucher toDomain(LegacyVoucherDocument legacyVoucher);
    LegacyVoucherDocument toDocument(LegacyVoucher voucher);
}
