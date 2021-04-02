package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.WaiverConverter;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.repository.WaiverRepository;
import in.skdv.skdvinbackend.service.IWaiverService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class MongoWaiverService implements IWaiverService {

    private WaiverRepository waiverRepository;
    private WaiverConverter waiverConverter = new WaiverConverter();

    @Autowired
    public MongoWaiverService(WaiverRepository waiverRepository) {
        this.waiverRepository = waiverRepository;
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
        WaiverDTO waiverDTO = waiverConverter.convertToDto(waiverRepository.save(waiverConverter.convertToEntity(waiver)));
        return new GenericResult<>(true, waiverDTO);
    }

    private boolean isMinorAndMissingParentsSignatures(WaiverDTO waiver) {
        return waiver.getWaiverCustomer().getDateOfBirth().isAfter(LocalDate.now().minusYears(18))
                && (waiver.getParentSignature1() == null || waiver.getParentSignature1().isBlank()
                || waiver.getParentSignature2() == null || waiver.getParentSignature2().isBlank());
    }
}
