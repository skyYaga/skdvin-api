package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.exception.VoucherAlreadyRedeemedException;
import in.skdv.skdvinbackend.model.domain.LegacyVoucher;
import in.skdv.skdvinbackend.model.entity.voucher.legacy.LegacyVoucherDocument;
import in.skdv.skdvinbackend.repository.LegacyVoucherRepository;
import in.skdv.skdvinbackend.service.ILegacyVoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class LegacyVoucherServiceTest extends AbstractSkdvinTest {

    @Autowired
    private ILegacyVoucherService legacyVoucherService;
    @Autowired
    private LegacyVoucherRepository legacyVoucherRepository;

    @BeforeEach
    void setup() {
        legacyVoucherRepository.deleteAll();
    }

    @Test
    void testGetVoucher() {
        legacyVoucherRepository.save(ModelMockHelper.createVoucherDocument());

        LegacyVoucher voucher = legacyVoucherService.findVoucher("10000");

        assertNotNull(voucher);
        assertEquals("10000", voucher.getId());
        assertEquals("John", voucher.getFirstName());
        assertEquals("Doe", voucher.getLastName());
    }


    @Test
    void testGetVoucher_notFound() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                legacyVoucherService.findVoucher("10001")
        );

        assertEquals(ErrorMessage.VOUCHER_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testRedeemVoucher() {
        LegacyVoucherDocument voucherDocument = ModelMockHelper.createVoucherDocument();
        voucherDocument.setRedeemed(false);
        voucherDocument.setRedeemDate(null);
        legacyVoucherRepository.save(voucherDocument);

        LegacyVoucher voucher = legacyVoucherService.redeemVoucher("10000");

        assertNotNull(voucher);
        assertEquals("10000", voucher.getId());
        assertNotNull(voucher.getRedeemDate());
        assertEquals(true, voucher.isRedeemed());
        assertEquals("John", voucher.getFirstName());
    }


    @Test
    void testRedeemVoucher_alreadyRedeemed() {
        LegacyVoucherDocument voucherDocument = ModelMockHelper.createVoucherDocument();
        voucherDocument.setRedeemed(true);
        voucherDocument.setRedeemDate(LocalDate.now().toString());
        legacyVoucherRepository.save(voucherDocument);

        VoucherAlreadyRedeemedException voucherAlreadyRedeemedException = assertThrows(VoucherAlreadyRedeemedException.class, () ->
                legacyVoucherService.redeemVoucher("10000")
        );

        assertEquals(ErrorMessage.VOUCHER_ALREADY_REDEEMED, voucherAlreadyRedeemedException.getErrorMessage());
    }
}
