package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.domain.LegacyVoucher;
import in.skdv.skdvinbackend.model.dto.voucher.legacy.LegacyVoucherDTO;
import in.skdv.skdvinbackend.model.mapper.LegacyVoucherMapper;
import in.skdv.skdvinbackend.service.ILegacyVoucherService;
import in.skdv.skdvinbackend.util.GenericResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/legacy-voucher")
@RequiredArgsConstructor
@Validated
public class LegacyVoucherController {

    private final ILegacyVoucherService legacyVoucherService;
    private final LegacyVoucherMapper legacyVoucherMapper;


    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_read:vouchers')")
    public GenericResult<LegacyVoucherDTO> readVoucher(@PathVariable @NotNull @Valid String id) {
        log.info("Reading voucher {}", id);
        LegacyVoucher legacyVoucher = legacyVoucherService.findVoucher(id);
        return new GenericResult<>(true, legacyVoucherMapper.toDto(legacyVoucher));
    }

    @PatchMapping(value = "/{id}/redeem")
    @PreAuthorize("hasAuthority('SCOPE_update:vouchers')")
    public GenericResult<LegacyVoucherDTO> redeemVoucher(@PathVariable @NotNull @Valid String id) {
        log.info("Redeeming voucher {}", id);
        LegacyVoucher legacyVoucher = legacyVoucherService.redeemVoucher(id);
        return new GenericResult<>(true, legacyVoucherMapper.toDto(legacyVoucher));
    }

}
