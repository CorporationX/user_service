package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;

import lombok.Setter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.dto.BanEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.event.ProfilePicEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.handler.EntityHandler;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.redisPublisher.ProfilePicEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final EntityHandler entityHandler;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;
    private final ObjectMapper objectMapper;
    private final ProfilePicEventPublisher profilePicEventPublisher;

//    @Transactional(readOnly = true)
//    public UserDto getUser(long userId) {
//        User user = entityHandler.getOrThrowException(User.class, userId, () -> userRepository.findById(userId));
//        return userMapper.toDto(user);
//    }

    @Transactional
    public UserDto createUser(UserDto userDto, MultipartFile userAvatar) {
        User user = userMapper.toEntity(userDto);
        user.setActive(true);
        user = userRepository.save(user);
        if (userAvatar == null) {
            avatarService.setRandomAvatar(user);
        } else {
            // todo: Gevorg's part
            profilePicEventPublisher.publish(new ProfilePicEvent(user.getId(), userAvatar.getOriginalFilename()));
        }
        return userMapper.toDto(userRepository.save(user));
    }

    public void uploadAvatar(long userId, UserProfilePicDto userProfilePicDto) {
        User user = userRepository.findById(userId).get();

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(userProfilePicDto.getFileId());
        userProfilePic.setSmallFileId(userProfilePicDto.getSmallFileId());
        user.setUserProfilePic(userProfilePic);

        userRepository.save(user);
    }

    @Transactional
    public void updateUserAvatar(long userId, MultipartFile multipartFile) {
        User user = entityHandler.getOrThrowException(User.class, userId, () -> userRepository.findById(userId));
        if (multipartFile == null) {
            avatarService.setRandomAvatar(user);
        } else {
            // todo: Добавление аватара пользователя; Will be done by Gevorg
            profilePicEventPublisher.publish(new ProfilePicEvent(userId, multipartFile.getOriginalFilename()));
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        userValidator.validateUserExistence(userId);

        return userMapper.toDto(userRepository.findById(userId).get());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        ids.forEach(userValidator::validateUserExistence);

        return userMapper.toDtoList(userRepository.findAllById(ids));
    }

    @Transactional(readOnly = true)

    public UserProfilePicDto getAvatarKeys(long userId) {
        userValidator.validateUserExistence(userId);

        UserProfilePic userProfilePic = userRepository.findById(userId).get().getUserProfilePic();
        return new UserProfilePicDto(userProfilePic.getFileId(), userProfilePic.getSmallFileId());
    }

    @Transactional
    public UserDto deactivateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        stopUserGoalActivities(user);
        stopPlannedEventActivities(user);
        user.setActive(false);
        stopMentorship(user);
        return userMapper.toDto(userRepository.save(user));
    }

    public boolean checkUserExistence(long userId) {
        return userRepository.existsById(userId);
    }

    public List<UserDto> getUserFollowers(long userId) {
        User user = entityHandler.getOrThrowException(User.class, userId, () -> userRepository.findById(userId));
        return user.getFollowers().stream().map(userMapper::toDto).toList();
    }

    public boolean checkAllFollowersExist(List<Long> followerIds) {
        return userValidator.doAllUsersExist(followerIds);
    }

    @Transactional
    public void banedUser(long userId) {
        userRepository.banUserById(userId);
    }

    @Transactional
    public void createBanEvent(Message message) {
        try {
            BanEvent banEvent = objectMapper.readValue(message.getBody(), BanEvent.class);
            banedUser(banEvent.getAuthorId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopUserGoalActivities(User user) {
        List<Goal> goalsToDelete = user.getGoals().stream()
                .peek(goal -> goal.getUsers().remove(user))
                .filter(goal -> goal.getUsers().isEmpty()).toList();
        goalRepository.deleteAll(goalsToDelete);
    }

    private void stopPlannedEventActivities(User user) {
        List<Event> cancelledEvents = user.getOwnedEvents().stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .peek(event -> event.setStatus(EventStatus.CANCELED)).toList();
        eventRepository.saveAll(cancelledEvents);
        user.getOwnedEvents().removeIf(event -> event.getStatus() == EventStatus.CANCELED);
        userRepository.save(user);
    }

    private void stopMentorship(User user) {
        user.getMentees().forEach(mentee -> {
            mentorshipService.deleteMentor(mentee.getId(), user.getId());
            setGoalsMentorAsMentee(mentee, user);
        });
    }

    private void setGoalsMentorAsMentee(User mentee, User mentor) {
        mentee.getGoals().stream()
                .filter(goal -> goal.getMentor().equals(mentor))
                .forEach(goal -> goal.setMentor(mentee));
    }

    public void deleteAvatar(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("there is no user with id: " + userId);
        }

        User user = userOptional.get();
        UserProfilePic userProfilePic = user.getUserProfilePic();
        userProfilePic.setSmallFileId(null);
        userProfilePic.setFileId(null);
        user.setUserProfilePic(userProfilePic);

        userRepository.save(user);
    }
}