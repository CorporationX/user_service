package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.PremiumAlreadyExistsException;

@Slf4j
@Component
public class PremiumValidator {

    public void validate(Long id, boolean existPremium) {
        if (existPremium) {
            log.warn("The premium already exists for the user with ID: {}", id);
            throw new PremiumAlreadyExistsException(id);
        }
        log.info("The premium does not exists for the user with ID: {}", id);
    }
}
