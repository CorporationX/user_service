package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.mapper.MapperUserDto;
import school.faang.user_service.messaging.MessagePublisher;
import school.faang.user_service.messaging.ProfileViewEventPublisher;
import school.faang.user_service.messaging.events.ProfileViewEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Locale;
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
        String message = String.format("Entity with ID %d not found", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(message));
        profileViewEventMessagePublisher.publish(new ProfileViewEvent(currentUserId, userId,
                PreferredContact.EMAIL));
//        profileViewEventMessagePublisher.publish(new ProfileViewEvent(currentUserId, userId,
//                user.getContactPreference().getPreference()));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> allById = userRepository.findAllById(ids);

        return allById.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
