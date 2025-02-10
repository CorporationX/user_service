package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.s3.AvatarS3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static school.faang.user_service.utils.user.UserErrorMessage.USERS_NOT_FOUND;
import static school.faang.user_service.utils.user.UserErrorMessage.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final AvatarS3Service avatarS3Service;
    private final UserAvatarService userAvatarService;
    private final UserContext userContext;

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public long getCurrentUserId() {
        return userContext.getUserId();
    }

    public User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, id)));
    }

    public List<User> getUsersByIds(List<User> users) {
        List<Long> userIds = users.stream()
                .map(User::getId)
                .toList();
        users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new IllegalArgumentException(USERS_NOT_FOUND);
        }
        return users;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with id = " + userId));
    }

    @Transactional
    public User registerUser(String username, String email, String password, Long countryId) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + countryId));

        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .country(country)
                .active(true)
                .experience(0)
                .build();

        userAvatarService.generateAvatarForNewUser(newUser, AvatarType.JPEG);

        return userRepository.save(newUser);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, userId)));

        deactivateUserDependencies(userId);

        user.setActive(false);
        userRepository.save(user);

        mentorshipService.stopUserMentorship(userId);
    }

    @Transactional
    public String uploadAvatar(MultipartFile file, String size) {
        long userId = userContext.getUserId();
        User currentUser = getUser(userId);

        Pair<UserProfilePic, String> uploadResult = avatarS3Service.uploadAvatar(file, size);

        if (currentUser.getUserProfilePic() != null) {
            String largeImageKey = currentUser.getUserProfilePic().getFileId();
            String smallImageKey = currentUser.getUserProfilePic().getSmallFileId();
            avatarS3Service.deleteAvatar(largeImageKey);
            avatarS3Service.deleteAvatar(smallImageKey);
        }

        currentUser.setUserProfilePic(uploadResult.getFirst());

        userRepository.save(currentUser);

        return uploadResult.getSecond();
    }

    @Transactional(readOnly = true)
    public String downloadAvatar(String size) {
        long userId = userContext.getUserId();
        User currentUser = getUser(userId);

        String imageKey = currentUser.getUserProfilePic().getFileId();
        if (size.equalsIgnoreCase("large")) {
            imageKey = currentUser.getUserProfilePic().getSmallFileId();
        }

        return avatarS3Service.downloadAvatar(imageKey);
    }

    @Transactional
    public void deleteAvatar() {
        long userId = userContext.getUserId();
        User currentUser = getUser(userId);

        String largeImageKey = currentUser.getUserProfilePic().getFileId();
        String smallImageKey = currentUser.getUserProfilePic().getSmallFileId();

        avatarS3Service.deleteAvatar(largeImageKey);
        avatarS3Service.deleteAvatar(smallImageKey);

        currentUser.setUserProfilePic(null);
        userRepository.save(currentUser);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = getUserById(userId);
        user.setBanned(true);
        userRepository.save(user);
        log.info("User with id {}. Was banned", userId);
    }

    private void deactivateUserDependencies(Long userId) {
        removeUserFromGoals(userId);
        removeUserEvents(userId);
    }

    private void removeUserFromGoals(Long userId) {
        List<Goal> userGoals = goalRepository.findGoalsByUserId(userId).toList();

        List<Goal> goalsToDelete = userGoals.stream()
                .filter(goal -> goal.getUsers().size() == 1)
                .toList();


        List<Goal> goalsToUpdate = userGoals.stream()
                .filter(goal -> goal.getUsers().size() > 1)
                .peek(goal -> goal.getUsers().removeIf(user -> Objects.equals(user.getId(), userId)))
                .toList();

        goalRepository.deleteAll(goalsToDelete);
        goalRepository.saveAll(goalsToUpdate);
    }

    private void removeUserEvents(Long userId) {
        List<Event> eventsOwnedToCancel = eventRepository.findAllByUserId(userId).stream()
                .filter(event -> Objects.equals(event.getOwner().getId(), userId))
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .toList();

        List<Event> eventsParticipatedToUpdate = eventRepository.findParticipatedEventsByUserId(userId).stream()
                .filter(event -> !Objects.equals(event.getOwner().getId(), userId))
                .peek(event -> event.getAttendees().removeIf(attendee ->
                        Objects.equals(attendee.getId(), userId)))
                .toList();

        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(eventsOwnedToCancel);
        allEvents.addAll(eventsParticipatedToUpdate);

        eventRepository.saveAll(allEvents);
    }
}
