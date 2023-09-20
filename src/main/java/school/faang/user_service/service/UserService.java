package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.mapper.MapperUserDto;
import school.faang.user_service.messaging.MessagePublisher;
import school.faang.user_service.messaging.events.ProfileViewEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final MapperUserDto userMapper;
    private final MessagePublisher<ProfileViewEvent> profileViewEventMessagePublisher;
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return applyFilter(userRepository.findPremiumUsers(), userFilterDto);
    }

    private List<UserDto> applyFilter(Stream<User> userList, UserFilterDto userFilterDto) {
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(userList, userFilterDto))
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUser(long currentUserId , long userId) {
        User user = findUserById(userId);
        profileViewEventMessagePublisher.publish(new ProfileViewEvent(currentUserId, userId, LocalDateTime.now()));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserInternal(long userId) {
        User user = findUserById(userId);

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> allById = userRepository.findAllById(ids);

        return allById.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public User findUserById(Long userId){
        String message = String.format("Entity with ID %d not found", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(message));
    }

    @Transactional
    public User saveUser(User user){
        return userRepository.save(user);
    }
}
