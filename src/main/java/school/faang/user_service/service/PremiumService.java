package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.PremiumActivatedDto;
import school.faang.user_service.dto.PremiumRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserService userService;

    public PremiumActivatedDto getPremiumForUserId(Long userId) {
        return premiumRepository.findByUserId(userId)
                .filter(premium -> premium.getEndDate().isAfter(LocalDateTime.now()))
                .map(this::premiumToDto)
                .orElse(null);
    }

    public PremiumActivatedDto subscribeToPremium(PremiumRequestDto premiumRequest) {
        validatePremium(premiumRequest);

        Long userId = premiumRequest.userId();
        Long days = premiumRequest.daysCount();

        User user = getUserById(userId);
        LocalDateTime premiumStartDate = LocalDateTime.now();
        LocalDateTime premiumEndDate = premiumStartDate.plusDays(days);

        Premium premium = new Premium();
        premium.setUser(user);
        premium.setStartDate(premiumStartDate);
        premium.setEndDate(premiumEndDate);
        premiumRepository.save(premium);

        log.info("Premium for user {} with start date {} and end date {} created",
                user.getUsername(), premiumStartDate, premiumEndDate );

        return premiumToDto(premium);
    }

    private User getUserById(long userId) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID: " + userId + " не найден."));
    }

    private PremiumActivatedDto premiumToDto(Premium premium) {
        return new PremiumActivatedDto(premium.getStartDate(), premium.getEndDate());
    }

    private void validatePremium(PremiumRequestDto premiumRequest) {
        Long userId = premiumRequest.userId();

        if (premiumRepository.existsByUserIdAndEndDateAfter(userId, LocalDateTime.now())) {
            throw new DataValidationException("Премиум для текущего пользователя уже имеется.");
        }
    }
}
