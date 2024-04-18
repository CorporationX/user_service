package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.dto.messagebroker.SearchAppearanceEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.UUID;

import school.faang.user_service.handler.exception.EntityNotFoundException;


import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.goal.GoalService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final GoalService goalService;
    private final EventRepository eventRepository;
    private final List<UserFilter> userFilters;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;

    @Autowired
    public UserService(UserMapper userMapper,
                       UserValidator userValidator,
                       UserRepository userRepository,
                       MentorshipService mentorshipService,
                       GoalService goalService,
                       EventRepository eventRepository,
                       @Qualifier("UserNameFilter") UserFilter userNameFilter,
                       @Qualifier("UserCountryFilter") UserFilter userCountryFilter,
                       SearchAppearanceEventPublisher searchAppearanceEventPublisher) {
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.mentorshipService = mentorshipService;
        this.goalService = goalService;
        this.eventRepository = eventRepository;
        this.searchAppearanceEventPublisher = searchAppearanceEventPublisher;
        userFilters = List.of(userNameFilter, userCountryFilter);
    }

    @Value("${dicebear.avatar}")
    private String avatarUrl;
    @Value("${dicebear.small_avatar}")
    private String smallAvatarUrl;

    public UserDto create(UserDto userDto) {
        userValidator.validatePassword(userDto);
        User user = userMapper.toEntity(userDto);
        user.setUserProfilePic(getRandomAvatar());
        user.setActive(true);
        User createdUser = userRepository.save(user);
        return userMapper.toDto(createdUser);
    }

    private UserProfilePic getRandomAvatar() {
        UUID uuid = UUID.randomUUID();
        return UserProfilePic.builder()
                .fileId(avatarUrl + uuid)
                .smallFileId(smallAvatarUrl + uuid)
                .build();
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %s not found", userId)));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userMapper.toDto(userRepository.findAllById(ids));
    }

    public UserDto deactivationUserById(Long userId) {
        User userDeactivate = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("Пользователь с id: " + userId + " не найден"));
        if (userDeactivate.getGoals() != null && !userDeactivate.getGoals().isEmpty()) {
            List<Long> deleteGoals = userDeactivate.getGoals().stream().filter(goal -> !GoalStatus.COMPLETED.equals(goal.getStatus()))
                    .peek(goal -> goal.getUsers().removeIf(user -> user.getId() == userId))
                    .filter(goal -> goal.getUsers().isEmpty())
                    .map(Goal::getId)
                    .toList();
            userDeactivate.setGoals(Collections.emptyList());
            deleteGoals.forEach(goalService::deleteGoal);
        }

        if (userDeactivate.getOwnedEvents() != null && !userDeactivate.getOwnedEvents().isEmpty()) {
            List<Long> deleteEvents = userDeactivate.getOwnedEvents().stream().filter(event -> EventStatus.PLANNED.equals(event.getStatus()))
                    .map(Event::getId)
                    .toList();
            userDeactivate.setOwnedEvents(Collections.emptyList());
            deleteEvents.forEach(eventRepository::deleteById);
        }

        userDeactivate.setActive(false);

        if (userDeactivate.getMentees() != null && !userDeactivate.getMentees().isEmpty()) {
            mentorshipService.deleteMentorForHisMentees(userId, userDeactivate.getMentees());
            userDeactivate.setMentees(Collections.emptyList());
        }

        User savedUser = userRepository.save(userDeactivate);
        return userMapper.toDto(savedUser);
    }

    public List<UserDto> searchUsersByFilter(UserFilterDto userFilterDto, Long requestUser) {
        List<User> userList = userRepository.findAll();
        List<UserDto> filteredUserList = userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(userList.stream(), userFilterDto))
                .map(userMapper::toDto)
                .toList();
        filteredUserList.forEach(user -> {
            SearchAppearanceEvent event = new SearchAppearanceEvent(
                    user.getId(),
                    requestUser,
                    LocalDateTime.now());
            searchAppearanceEventPublisher.publish(event);
        });
        return filteredUserList;
    }
}
