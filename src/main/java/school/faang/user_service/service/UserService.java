package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PremiumRepository premiumRepository;
    private final List<UserFilter> userFilters;

    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new DataValidationException("Пользователя не существует"));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id " + userId + " has not found"));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                premiumUsers = filter.apply(premiumUsers.stream(), filters).toList();
            }
        }

        return userMapper.toDto(premiumUsers);
    }

    public void validateUserDoesNotHavePremium(long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("Пользователь уже имеет премиум подписку");
        }
    }
}
