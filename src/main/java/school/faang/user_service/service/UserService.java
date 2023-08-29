package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.entity.User;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final User1Mapper mapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final List<UserFilter> userFilters;
    private final ContactService contactService;

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
        return  userNotificationDto;
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
    public void setUserTelegramId(long userId, long telegramId){
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
}
