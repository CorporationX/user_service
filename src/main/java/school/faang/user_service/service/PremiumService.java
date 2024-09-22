package school.faang.user_service.service;

import school.faang.user_service.dto.premiun.PremiumDto;
import school.faang.user_service.dto.premiun.PremiumRequest;

public interface PremiumService {

    PremiumDto buyPremium(PremiumRequest request, long id);
}
