package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final UserRepository userRepo;

    public void validatePremium(Long userId) {
        User user = userRepo.findById(userId).get();
        if (user.getPremium() != null) {
            throw new IllegalArgumentException("User already has a premium");
        }

    }
}
