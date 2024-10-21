package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.UserAlreadyExistsException;
import school.faang.user_service.exception.user.UserDeactivatedException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final GoalService goalService;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final AvatarService avatarService;
    private final List<UserFilter> userFilters;

    @Transactional
    public User registerUser(User user) {
        validateUsernameAndEmail(user);
        UserProfilePic userProfilePic = avatarService.generateAndSaveAvatar(AvatarStyle.BOTTTTS);
        user.setUserProfilePic(userProfilePic);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);

        if (!user.isActive()) {
            throw new UserDeactivatedException();
        }

        removeUserGoals(user);
        removeUserEvents(user);
        mentorshipService.deleteMentorFromMentees(user.getId(), user.getMentees());

        user.setActive(false);
        userRepository.save(user);
    }

    @PublishEvent(returnedType = User.class)
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void bannedUser(Long userId) {
        User user = findById(userId);
        user.setBanned(true);
        log.info("User banned: {}", userId);
        userRepository.save(user);
    }

    private void removeUserGoals(User user) {
        user.getGoals().forEach(goal -> {
            if (goal.getUsers().size() == 1) {
                goalService.deleteGoalAndUnlinkChildren(goal);
            }

            goal.getUsers().remove(user);
        });

        user.getGoals().clear();
    }

    private void removeUserEvents(User user) {
        if (user.getOwnedEvents() == null) {
            return;
        }

        List<Event> plannedEvents = user.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus().equals(EventStatus.PLANNED))
                .toList();

        plannedEvents.forEach(event -> {
            event.setStatus(EventStatus.CANCELED);
            eventRepository.save(event);
            eventRepository.delete(event);
        });

    }

    private void validateUsernameAndEmail(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }
    }

    @PublishEvent(returnedType = User.class)
    @Transactional(readOnly = true)
    public List<User> getPremiumUsers(UserFilterDto userFilterDto) {
        log.info("Find premium users by filter: {}", userFilterDto.toString());
        List<Premium> premiums = premiumRepository.findAll();
        Stream<User> users = premiums.stream().map(Premium::getUser);
        return filterUsers(users, userFilterDto);
    }

    private List<User> filterUsers(Stream<User> users, UserFilterDto userFilterDto) {
        return userFilters
                .stream()
                .filter(f -> f.isApplicable(userFilterDto))
                .reduce(users,
                        (stream, filter) -> filter.apply(stream, userFilterDto),
                        (s1, s2) -> s1)
                .toList();
    }

    @PublishEvent(returnedType = User.class)
    @Transactional(readOnly = true)
    public List<User> getUsers(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @PublishEvent(returnedType = User.class)
    @Transactional(readOnly = true)
    public User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Long> getOnlyActiveUsersFromList(List<Long> ids) {
        if (isEmpty(ids)) {
            throw new IllegalArgumentException("User ID list cannot be null or empty");
        }
        return userRepository.findActiveUserIds(ids);
    }
}
