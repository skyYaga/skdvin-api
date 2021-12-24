package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.waiver.WaiverState;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import in.skdv.skdvinbackend.repository.WaiverRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IWaiverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class WaiverService implements IWaiverService {

    private final WaiverRepository waiverRepository;
    private final ISettingsService settingsService;

    @Override
    public List<Waiver> getWaivers() {
        return waiverRepository.findAll();
    }

    @Override
    public Waiver saveWaiver(Waiver waiver) {
        checkMinorAndMissingParentsSignatures(waiver);

        waiver.setWaiverText(settingsService.getWaiverSettingsByLanguage(LocaleContextHolder.getLocale().getLanguage()).getTandemwaiver());

        return waiverRepository.save(waiver);
    }

    @Override
    public Waiver updateWaiver(Waiver waiver) {
        Optional<Waiver> existingWaiverOptional = waiverRepository.findById(waiver.getId());
        if (existingWaiverOptional.isEmpty()) {
            log.error("Waiver {} not found.", waiver.getId());
            throw new NotFoundException(ErrorMessage.WAIVER_NOT_EXISTING);
        }
        if (waiver.getTandemmaster() != null && !waiver.getTandemmaster().isBlank()) {
            waiver.setState(WaiverState.ASSIGNED);
        }
        if (waiver.getTandemmasterSignature() != null && !waiver.getTandemmasterSignature().isBlank()) {
            waiver.setState(WaiverState.CONFIRMED);
        }
        return saveWaiver(waiver);
    }

    private void checkMinorAndMissingParentsSignatures(Waiver waiver) {
        if (waiver.getWaiverCustomer().getDateOfBirth().isAfter(LocalDate.now().minusYears(18))
                && (waiver.getParentSignature1() == null || waiver.getParentSignature1().isBlank()
                || waiver.getParentSignature2() == null || waiver.getParentSignature2().isBlank())) {
            log.error("Waiver missing parent signatures {}", waiver);
            throw new InvalidRequestException(ErrorMessage.WAIVER_MINOR_MISSING_SIGNATURES);
        }
    }
}
