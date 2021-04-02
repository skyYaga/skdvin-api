package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.WaiverConverter;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import in.skdv.skdvinbackend.repository.WaiverRepository;
import in.skdv.skdvinbackend.service.IWaiverService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.stream.Stream;

@SpringBootTest
public class MongoWaiverServiceTest extends AbstractSkdvinTest {

    private WaiverConverter converter = new WaiverConverter();

    @Autowired
    private WaiverRepository waiverRepository;

    @Autowired
    IWaiverService waiverService;

    @BeforeEach
    void setup() {
        waiverRepository.deleteAll();
    }

    @Test
    void saveWaiver() {
        Waiver waiver = ModelMockHelper.createWaiver();

        GenericResult<WaiverDTO> savedWaiver = waiverService.saveWaiver(converter.convertToDto(waiver));

        Assertions.assertTrue(savedWaiver.isSuccess());
        Assertions.assertNotNull(savedWaiver.getPayload().getId());
        Assertions.assertEquals(1, waiverService.getWaivers().size());
    }

    @Test
    void saveWaiver_Minors() {
        Waiver waiver = ModelMockHelper.createWaiver();
        waiver.getWaiverCustomer().setDateOfBirth(LocalDate.now().minusYears(16));
        waiver.setParentSignature1("data:signatureParent1");
        waiver.setParentSignature2("data:signatureParent2");

        GenericResult<WaiverDTO> savedWaiver = waiverService.saveWaiver(converter.convertToDto(waiver));

        Assertions.assertTrue(savedWaiver.isSuccess());
        Assertions.assertNotNull(savedWaiver.getPayload().getId());
        Assertions.assertEquals(1, waiverService.getWaivers().size());
    }

    @ParameterizedTest
    @MethodSource("provideStringsForMinorAndParentSignaturesMissing")
    void saveWaiver_ErrorWhenMinorAndParentSignaturesMissing(String sig1, String sig2) {
        Waiver waiver = ModelMockHelper.createWaiver();
        waiver.getWaiverCustomer().setDateOfBirth(LocalDate.now().minusYears(16));
        waiver.setParentSignature1(sig1);
        waiver.setParentSignature2(sig2);

        GenericResult<WaiverDTO> savedWaiver = waiverService.saveWaiver(converter.convertToDto(waiver));

        Assertions.assertFalse(savedWaiver.isSuccess());
        Assertions.assertEquals(ErrorMessage.WAIVER_MINOR_MISSING_SIGNATURES.toString(), savedWaiver.getMessage());
        Assertions.assertNull(savedWaiver.getPayload());
    }

    private static Stream<Arguments> provideStringsForMinorAndParentSignaturesMissing() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", ""),
                Arguments.of("", null),
                Arguments.of(null, "")
        );
    }

}
