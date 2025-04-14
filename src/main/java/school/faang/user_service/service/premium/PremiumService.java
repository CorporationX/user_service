package school.faang.user_service.service.premium;

import school.faang.user_service.entity.premium.Premium;

import java.util.List;

public interface PremiumService {

    void removeExpiredPremiums();

    void deleteBatch(List<Premium> batch);
}