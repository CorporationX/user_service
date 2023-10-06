package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.mapper.mymappers.User1Mapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final User1Mapper mapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final List<UserFilter> userFilters;
    private final ContactService contactService;
    private final UserProfilePicService userProfilePicService;

    public UserDto getUser(long id) {
        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        return mapper.toDto(foundUser);
    }

    public UserNotificationDto getUserForNotification(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        var a = user.getContactPreference().getPreference();
        var userNotificationDto = mapper.toNotificationDto(user);
        userNotificationDto.setPreference(a);
        return userNotificationDto;
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        List<User> foundUsers = userRepository.findAllById(userIds);

        return mapper.toDtos(foundUsers);
    }

    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> premiumUserStream = userRepository.findPremiumUsers();

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filterDto)) {
                premiumUserStream = userFilter.apply(premiumUserStream, filterDto);
            }
        }

        return premiumUserStream.map(mapper::toDto).toList();
    }

    @Transactional
    public void setUserTelegramId(long userId, long telegramId) {
        var optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            var contact = Contact.builder().contact(Long.toString(telegramId)).user(user).type(ContactType.TELEGRAM).build();
            contactService.save(contact);
            user.getContacts().add(contact);
        });
    }

    @Transactional
    public UserDto deactivateUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id: " + userId + " wasn`t found")
        );

        removeMentees(user);
        removeGoals(user);
        removeEvents(user);

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(userRepository.save(user));

    }

    private void removeMentees(User user) {
        if (user.getMentees() == null) return;
        user.getMentees().forEach(mentee -> {
            mentee.getMentors().remove(user);
            var goals = mentee.getSetGoals();
            if (goals != null) {
                goals.forEach(goal -> {
                            if (goal.getMentor() != null && goal.getMentor().getId() == user.getId()) {
                                goal.setMentor(null);
                                goalService.save(goal);
                            }
                        }
                );
            }
        });
    }

    private void removeGoals(User user) {
        if (user.getGoals() == null) return;
        user.getGoals().forEach(goal -> {
            goal.getUsers().remove(user);
            if (goal.getUsers().isEmpty()) {
                goalService.deleteGoal(goal.getId());
            }
        });
    }

    private void removeEvents(User user) {
        if (user.getOwnedEvents() == null) return;
        user.getOwnedEvents().forEach(event -> {
            event.setStatus(EventStatus.CANCELED);
            eventService.save(event);
        });
    }

    @Transactional
    public void userBanEventSave(String message) {
        Long userId = Long.valueOf(message);
        Optional<User> userById = userRepository.findById(userId);
        User user = userById.get();
        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public UserProfilePic saveAvatar(long userId, MultipartFile multipartFile) {
        Optional<User> userById = userRepository.findById(userId);
        User user = userById.get();
        UserProfilePic uploadAvatar = userProfilePicService.upload(multipartFile);
        user.setUserProfilePic(uploadAvatar);
        userRepository.save(user);
        return uploadAvatar;
    }

    @Transactional
    public void deleteProfilePic(Long userId) {
        Optional<User> userById = userRepository.findById(userId);
        User user = userById.get();
        if (user.getUserProfilePic() != null) {
            UserProfilePic userProfilePic = user.getUserProfilePic();
            userProfilePicService.deleteAvatar(userProfilePic.getFileId());
            userProfilePicService.deleteAvatar(userProfilePic.getSmallFileId());
            user.setUserProfilePic(null);
            userRepository.save(user);
        }
    }
}
