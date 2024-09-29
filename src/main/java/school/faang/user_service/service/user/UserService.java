package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.promotion.PromotionService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final PromotionService promotionService;

    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with this id does not exist in the database"));
    }

    @Transactional
    public UserDto getUser(long userId) {
        User existedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " does not exist"));

        return userMapper.toDto(existedUser);
    }

    @Transactional
    public List<UserDto> getUsersByIds(List<Long> ids) {
        ids.forEach(userValidator::validateUserIdIsPositiveAndNotNull);

        return userMapper.toDtos(userRepository.findAllById(ids));
    }

    @Transactional
    public List<User> getUsersById(List<Long> usersId) {
        return userRepository.findAllById(usersId);
    }

    @Transactional
    public List<UserDto> users(UserFilterDto filterDto) {
        promotionService.removeExpiredPromotions();

        List<User> filteredUsers = filteredUsers(filterDto);
        List<User> prioritizedUsers = prioritizedUsers(filteredUsers);

        markAsShownUsers(prioritizedUsers);

        return prioritizedUsers.stream()
                .map(userMapper::toDto)
                .toList();
    }

    private List<User> filteredUsers(UserFilterDto filterDto) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(userRepository::findAll)
                .orElseGet(Collections::emptyList);
    }

    private List<User> prioritizedUsers(List<User> users) {
        return users.stream()
                .sorted(Comparator.comparingInt(this::userPriority).reversed())
                .collect(Collectors.toList());
    }

    private Integer userPriority(User user) {
        return user.getPromotions().stream()
                .filter(promotion -> PromotionTarget.PROFILE.name().equals(promotion.getTarget()))
                .findFirst()
                .map(Promotion::getPriority)
                .orElse(0);
    }

    private void markAsShownUsers(List<User> users) {
        List<Long> promotionIds = users.stream()
                .map(User::getPromotions)
                .filter(promotions -> !promotions.isEmpty())
                .flatMap(promotions -> {
                    return promotions.stream().filter(promotion -> PromotionTarget.PROFILE.name().equals(promotion.getTarget()));
                })
                .map(Promotion::getId)
                .toList();

        promotionService.markAsShowPromotions(promotionIds);
    }
}
