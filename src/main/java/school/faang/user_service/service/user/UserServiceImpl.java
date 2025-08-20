package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.avatar.service.UserAvatarService;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.messaging.dto.ProfileVisitEvent;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;
import school.faang.user_service.messaging.producer.EventPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.filter.FilterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final FilterService<User, UserFilterDto> filterService;
    private final UserAvatarService avatarService;
    private final EventPublisher<SearchAppearanceEvent> searchAppearanceEventPublisher;
    private final EventPublisher<ProfileVisitEvent> profileVisitAppearanceEventPublisher;
    private final ExecutorService executor;

    @Override
    @Transactional
    public UserDto create(UserCreateDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);

        user = userRepository.save(user);
        setAvatarIfPossible(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    private void setAvatarIfPossible(User user) {
        try {
            String avatarUrl = avatarService.generateAndUpload(user.getUsername()).getUrl();
            user.setAvatarUrl(avatarUrl);
            log.info("Generated avatar for user {}: {}", user.getId(), avatarUrl);
        } catch (Exception e) {
            log.warn("Failed to generate avatar for user {}: {}", user.getId(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserUpdateDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long userId) {
        var currentUserId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);
        var event = new ProfileVisitEvent(currentUserId, userId, LocalDateTime.now());
        publishEvent(profileVisitAppearanceEventPublisher, event);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public List<UserDto> getUsers(UserFilterDto filter) {
        var currentUserId = userContext.getUserId();
        Stream<User> users = null;
        if (filter.onlyPremium()) {
            users = userRepository.findPremiumUsers();
        } else {
            users = userRepository.findAll().stream();
        }

        users = filterService.getFilteredList(users.toList(), filter).stream();
        var now = LocalDateTime.now();
        var result = users.map(userMapper::toUserDto)
                .toList();
        result.stream()
                .map((user) -> new SearchAppearanceEvent(currentUserId, user.id(), now))
                .forEach((event) -> publishEvent(searchAppearanceEventPublisher, event));
        return result;
    }

    private <E> void publishEvent(EventPublisher<E> publisher, E event) {
        CompletableFuture.runAsync(() -> publisher.publish(event), executor)
                .exceptionally(ex -> {
                    log.error("Ошибка публикации события {}", ex.getMessage(), ex);
                    return null;
                });
    }
}
