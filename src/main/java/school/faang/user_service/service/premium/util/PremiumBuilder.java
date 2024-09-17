package school.faang.user_service.service.premium.util;

import lombok.experimental.UtilityClass;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;

import java.time.LocalDateTime;

@UtilityClass
public class PremiumBuilder {

    public static Premium buildPremium(User user, PremiumPeriod period) {
        return Premium
                .builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
    }
}
