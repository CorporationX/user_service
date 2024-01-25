package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PremiumRepository premiumRepository;

    public void validateUserDoesNotHavePremium(long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("Пользователь уже имеет премиум подписку");
        }
    }
    public UserDto getUser(long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new DataValidationException("Пользователя не существует"));
    }
}
