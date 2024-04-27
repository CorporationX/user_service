package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.UserEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static school.faang.user_service.validator.user.UserConstraints.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final GoalService goalService;
    private final EventRepository eventRepository;
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

    public void banUser(UserEvent userEvent){
        User user = getUser(userEvent.getUserId());
        user.setBanned(true);
        userRepository.save(user);
        log.info(String.format("User with id:%d banned :)", userEvent.getUserId()));
    }

    private UserProfilePic getRandomAvatar() {
        UUID uuid = UUID.randomUUID();
        return UserProfilePic.builder()
                .fileId(avatarUrl + uuid)
                .smallFileId(smallAvatarUrl + uuid)
                .build();
    }

    public UserDto getUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %s not found", userId)));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersDtoByIds(List<Long> ids) {
        return userMapper.toDto(userRepository.findAllById(ids));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND.getMessage(), userId)));
    }

    public UserDto deactivationUserById(Long userId) {
        User userDeactivate = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("Пользователь с id: " + userId + " не найден"));
        if (userDeactivate.getGoals() != null && !userDeactivate.getGoals().isEmpty()) {
            List<Long> deleteGoals = userDeactivate.getGoals().stream().filter(goal -> !GoalStatus.COMPLETED.equals(goal.getStatus()))
                    .peek(goal -> goal.getUsers().removeIf(user -> user.getId() == userId))
                    .filter(goal -> goal.getUsers().isEmpty())
                    .map(goal -> goal.getId())
                    .toList();
            userDeactivate.setGoals(Collections.emptyList());
            deleteGoals.forEach(deleteGoal -> goalService.deleteGoal(deleteGoal));
        }

        if (userDeactivate.getOwnedEvents() != null && !userDeactivate.getOwnedEvents().isEmpty()) {
            List<Long> deleteEvents = userDeactivate.getOwnedEvents().stream().filter(event -> EventStatus.PLANNED.equals(event.getStatus()))
                    .map(event -> event.getId())
                    .toList();
            userDeactivate.setOwnedEvents(Collections.emptyList());
            deleteEvents.forEach(deleteEvent -> eventRepository.deleteById(deleteEvent));
        }

        userDeactivate.setActive(false);

        if (userDeactivate.getMentees() != null && !userDeactivate.getMentees().isEmpty()) {
            mentorshipService.deleteMentorForHisMentees(userId, userDeactivate.getMentees());
            userDeactivate.setMentees(Collections.emptyList());
        }

        User savedUser = userRepository.save(userDeactivate);
        return userMapper.toDto(savedUser);
    }
}
