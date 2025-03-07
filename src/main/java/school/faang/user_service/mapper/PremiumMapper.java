package school.faang.user_service.mapper;

import school.faang.user_service.dto.PremiumActivated;
import school.faang.user_service.entity.premium.Premium;

public interface PremiumMapper {
    PremiumActivated premiumToPremiumActivated(Premium premium);
}
