package school.faang.user_service.service.premium.util;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.PremiumPeriod;

import java.time.LocalDateTime;

@Component
public class PremiumBuilder {

    public Premium getPremium(User user, PremiumPeriod period) {
        return Premium
                .builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
    }
}
