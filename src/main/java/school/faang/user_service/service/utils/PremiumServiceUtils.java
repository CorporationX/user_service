package school.faang.user_service.service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PremiumServiceUtils {
    private final PremiumRepository premiumRepository;

    private final UserService userService;

    public void checkUserHasNoPremium(Long userId) {
        User user = userService.getUserById(userId);
        if (user.getPremium() != null
        && LocalDateTime.now().isBefore(user.getPremium().getEndDate())) {
            throw new RuntimeException("User with id " + userId + " has premium");
        }
    }

    public void assignPremiumToUser(Long userId, Integer durationDays) {
        userService.getUserById(userId).setPremium(create(userId, durationDays));
    }

    private Premium create(Long userId, Integer durationDays) {
        return premiumRepository
                .save(
                        Premium.builder()
                                .user(userService.getUserById(userId))
                                .startDate(LocalDateTime.now())
                                .endDate(LocalDateTime.now().plusDays(durationDays))
                                .build()
                );
    }
}
