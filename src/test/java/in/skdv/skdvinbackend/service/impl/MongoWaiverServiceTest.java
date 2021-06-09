package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.waiver.WaiverState;
import in.skdv.skdvinbackend.model.converter.WaiverConverter;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.model.entity.settings.WaiverSettings;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import in.skdv.skdvinbackend.repository.WaiverRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
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
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class MongoWaiverServiceTest extends AbstractSkdvinTest {

    private WaiverConverter converter = new WaiverConverter();

    @Autowired
    private WaiverRepository waiverRepository;

    @Autowired
    IWaiverService waiverService;

    @MockBean
    ISettingsService settingsService;

    private final WaiverSettings waiverSettings = new WaiverSettings();

    @BeforeEach
    void setup() {
        waiverSettings.setTandemwaiver("Tandem Waiver Text");
        when(settingsService.getWaiverSettingsByLanguage(anyString())).
                thenReturn(waiverSettings);
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
    void saveWaiver_AddsText() {
        Waiver waiver = ModelMockHelper.createWaiver();

        GenericResult<WaiverDTO> savedWaiver = waiverService.saveWaiver(converter.convertToDto(waiver));

        Assertions.assertTrue(savedWaiver.isSuccess());
        Assertions.assertEquals(waiverSettings.getTandemwaiver(), savedWaiver.getPayload().getWaiverText());
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

    @Test
    void updateWaiver_AssignTandemmaster() {
        Waiver waiver = ModelMockHelper.createWaiver();
        WaiverDTO savedWaiver = waiverService.saveWaiver(converter.convertToDto(waiver)).getPayload();

        Assertions.assertEquals(WaiverState.NEW, savedWaiver.getState());
        savedWaiver.setTandemmaster("foo");

        GenericResult<WaiverDTO> updatedWaiver = waiverService.updateWaiver(savedWaiver);
        Assertions.assertTrue(updatedWaiver.isSuccess());
        Assertions.assertEquals("foo", updatedWaiver.getPayload().getTandemmaster());
        Assertions.assertEquals(WaiverState.ASSIGNED, savedWaiver.getState());
    }

    @Test
    void updateWaiver_TandemmasterSigned() {
        Waiver waiver = ModelMockHelper.createWaiver();
        WaiverDTO savedWaiver = waiverService.saveWaiver(converter.convertToDto(waiver)).getPayload();
        savedWaiver.setTandemmaster("foo");
        savedWaiver = waiverService.updateWaiver(savedWaiver).getPayload();

        Assertions.assertEquals(WaiverState.ASSIGNED, savedWaiver.getState());

        savedWaiver.setTandemmasterSignature("data:signatureTM");

        GenericResult<WaiverDTO> updatedWaiver = waiverService.updateWaiver(savedWaiver);
        Assertions.assertTrue(updatedWaiver.isSuccess());
        Assertions.assertEquals("data:signatureTM", updatedWaiver.getPayload().getTandemmasterSignature());
        Assertions.assertEquals(WaiverState.CONFIRMED, savedWaiver.getState());
    }

    @Test
    void updateWaiver_ErrorWhenWaiverNotExisting() {
        WaiverDTO waiverDTO = converter.convertToDto(ModelMockHelper.createWaiver());
        waiverDTO.setId("anyID");
        GenericResult<WaiverDTO> updatedWaiver = waiverService.updateWaiver(waiverDTO);

        Assertions.assertFalse(updatedWaiver.isSuccess());
        Assertions.assertEquals(ErrorMessage.WAIVER_NOT_EXISTING.toString(), updatedWaiver.getMessage());
        Assertions.assertNull(updatedWaiver.getPayload());
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
