package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.annotation.PublishProfileViewEvent;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.event.AnalyticsProfileViewEvent;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.s3.AvatarS3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static school.faang.user_service.utils.user.UserErrorMessage.USERS_NOT_FOUND;
import static school.faang.user_service.utils.user.UserErrorMessage.USER_NOT_FOUND;

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
    private final UserMapper userMapper;

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public long getCurrentUserId() {
        return userContext.getUserId();
    }

    @PublishProfileViewEvent(events = AnalyticsProfileViewEvent.class)
    @Transactional(readOnly = true)
    public User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, id)));
    }

    @PublishProfileViewEvent(events = AnalyticsProfileViewEvent.class)
    @Transactional(readOnly = true)
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

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with id = " + userId));
    }

    @Transactional
    public User registerUser(String username, String email, String password, Long countryId, String telegramUsername) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + countryId));

        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .country(country)
                .telegramUsername(telegramUsername)
                .active(true)
                .experience(0)
                .build();

        userAvatarService.generateAvatarForNewUser(newUser, AvatarType.JPEG);

        return userRepository.save(newUser);
    }

    @Transactional
    public void setBannedField(long userId, boolean banned) {
        userRepository.setBannedField(userId, banned);
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

    private void chooseNotificationMethode(Long userId, PreferredContact preferredContact) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_FOUND, userId)));
        user.setPreference(preferredContact);
        userRepository.save(user);
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

    @Transactional
    public Page<UserDto> getUsersByIds(List<Long> ids, Pageable pageable) {
        List<User> users = userRepository.findByIdIn(ids, pageable);
        List<UserDto> userDos = users
                .stream()
                .map(userMapper::toDto)
                .toList();
        return new PageImpl<>(userDos, pageable, userRepository.countByIdIn(ids));
    }

    public User updateTelegramData(String telegramUsername, String telegramChatId) {
        User user = userRepository.findByTelegramUsername(telegramUsername).orElseThrow(
                () -> new IllegalArgumentException(String.format(USER_NOT_FOUND, telegramChatId)));
        user.setTelegramChatId(telegramChatId);
        return userRepository.save(user);
    }

    private Double getUserRatingScore(Long userId) {
        return getUser(userId).getRatingScore();
    }

    public Double addUserRatingScore(Long userId, Double score) {
        Optional<Double> userRating = Optional.ofNullable(getUserRatingScore(userId));
        double newScore = userRating.orElse(0.0) + score;

        if (newScore < 0) {
            newScore = 0.0;
        }

        User user = getUser(userId);
        user.setRatingScore(newScore);
        updateUser(user);

        return newScore;
    }

    public Map<UserDto, Double> findAllUsersWithRatingScores(int topUsersLimit) {
        return userRepository.findAllUsersWithRatingScores(PageRequest.ofSize(topUsersLimit)
                        .withSort(Sort.by("ratingScore").descending()))
                .stream().collect(Collectors.toMap(userMapper::toDto, User::getRatingScore));
    }

    @Transactional
    public void resetAllRatingScores() {
        userRepository.resetAllRatingScores();
    }
}
