package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.waiver.WaiverState;
import in.skdv.skdvinbackend.model.converter.WaiverConverter;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import in.skdv.skdvinbackend.repository.WaiverRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IWaiverService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MongoWaiverService implements IWaiverService {

    private WaiverRepository waiverRepository;
    private ISettingsService settingsService;
    private WaiverConverter waiverConverter = new WaiverConverter();

    @Autowired
    public MongoWaiverService(WaiverRepository waiverRepository, ISettingsService settingsService) {
        this.waiverRepository = waiverRepository;
        this.settingsService = settingsService;
    }

    @Override
    public List<WaiverDTO> getWaivers() {
        return waiverConverter.convertToDto(waiverRepository.findAll());
    }

    @Override
    public GenericResult<WaiverDTO> saveWaiver(WaiverDTO waiver) {
        if (isMinorAndMissingParentsSignatures(waiver)) {
            return new GenericResult<>(false, ErrorMessage.WAIVER_MINOR_MISSING_SIGNATURES);
        }

        waiver.setWaiverText(settingsService.getWaiverSettingsByLanguage(LocaleContextHolder.getLocale().getLanguage()).getTandemwaiver());

        WaiverDTO waiverDTO = waiverConverter.convertToDto(waiverRepository.save(waiverConverter.convertToEntity(waiver)));
        return new GenericResult<>(true, waiverDTO);
    }

    @Override
    public GenericResult<WaiverDTO> updateWaiver(WaiverDTO waiver) {
        Optional<Waiver> existingWaiverOptional = waiverRepository.findById(waiver.getId());
        if (existingWaiverOptional.isPresent()) {
            if (waiver.getTandemmaster() != null && !waiver.getTandemmaster().isBlank()) {
                waiver.setState(WaiverState.ASSIGNED);
            }
            if (waiver.getTandemmasterSignature() != null && !waiver.getTandemmasterSignature().isBlank()) {
                waiver.setState(WaiverState.CONFIRMED);
            }
            return saveWaiver(waiver);
        }

        return new GenericResult<>(false, ErrorMessage.WAIVER_NOT_EXISTING);
    }

    private boolean isMinorAndMissingParentsSignatures(WaiverDTO waiver) {
        return waiver.getWaiverCustomer().getDateOfBirth().isAfter(LocalDate.now().minusYears(18))
                && (waiver.getParentSignature1() == null || waiver.getParentSignature1().isBlank()
                || waiver.getParentSignature2() == null || waiver.getParentSignature2().isBlank());
    }
}
