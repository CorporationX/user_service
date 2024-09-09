package school.faang.user_service.util;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;

public class PremiumFabric {

    public static Premium getPremium(long id, User user, LocalDateTime startDate, LocalDateTime endDate) {
        return Premium
                .builder()
                .id(id)
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static Premium getPremium(long id, LocalDateTime startDate, LocalDateTime endDate) {
        return Premium
                .builder()
                .id(id)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static User getUser(long id, Premium premium) {
        return User
                .builder()
                .id(id)
                .premium(premium)
                .build();
    }

    public static User getUser(long userId) {
        return User
                .builder()
                .id(userId)
                .build();
    }
}
