package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.voucher.legacy.LegacyVoucherDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LegacyVoucherRepository extends MongoRepository<LegacyVoucherDocument, String> {

}
