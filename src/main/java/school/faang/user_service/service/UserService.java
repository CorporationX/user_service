package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.DeactivateResponseDto;
import school.faang.user_service.dto.contact.ExtendedContactDto;
import school.faang.user_service.dto.contact.TgContactDto;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DeactivationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    @Value("${dicebear.url}")
    private String dicebearUrl;

    public UserDto getUser(long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            throw new UserNotFoundException("User with id " + id + " not found");
        });
        log.info("Return user with id: {}", foundUser.getId());
        return userMapper.toUserDto(foundUser);
    }

    public List<UserDto> getUsersByIds(List<Long> usersIds) {
        List<User> users = userRepository.findAllById(usersIds);
        log.info("Return list of users: {}", users);
        return userMapper.toUserListDto(users);
    }

    public UserDto signup(UserDto userDto) {
        //TODO Нужно реализовать логику создания юзера
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UserNotFoundException("User is not found"));
        setDefaultAvatar(user);
        return userDto;
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();

        premiumUsers = filter(userFilterDto, premiumUsers);
        return userMapper.toUserListDto(premiumUsers.toList());
    }

    @Transactional
    public DeactivateResponseDto deactivateUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DeactivationException("there is no user", userId));
        if (!user.isActive()) {
            throw new DeactivationException("The user has already been deactivated", userId);
        }
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();
        goals.stream().filter(goal -> goal.getUsers().size() == 1).forEach(goalRepository::delete);

        goals.forEach(goal -> deleteUser(goal, user));
        goalRepository.saveAll(goals);

        List<Event> eventList = eventRepository.findAllByUserId(userId);
        eventRepository.deleteAll(eventList.stream().filter(event -> event.getOwner().getId() == userId).toList());

        mentorshipService.cancelMentoring(user, goals);

        user.setActive(false);
        userRepository.save(user);
        return new DeactivateResponseDto("The user is deactivated", userId);
    }

    @Transactional
    public void banUser(long id) {
        userRepository.banUser(id);
    }

    private void deleteUser(Goal goal, User user) {
        List<User> users = new ArrayList<>(goal.getUsers());
        users.remove(user);
        goal.setUsers(users);
    }

    private Stream<User> filter(UserFilterDto userFilterDto, Stream<User> premiumUsers) {
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(userFilterDto)) {
                premiumUsers = filter.apply(premiumUsers, userFilterDto);
            }
        }
        return premiumUsers;
    }

    private void setDefaultAvatar(User user) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(dicebearUrl + user.getUsername() + "&scale=" + 130);
        userProfilePic.setSmallFileId(dicebearUrl + user.getUsername() + "&scale=" + 22);
        user.setUserProfilePic(userProfilePic);
    }

    public void updateUserContact(TgContactDto tgContactDto) {
        User user = userRepository.findById(tgContactDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("No user found by this id: " + tgContactDto.getUserId()));
        Contact contact = user.getContacts()
                .stream().filter(c -> c.getType().equals(ContactType.TELEGRAM))
                .findFirst()
                .orElse(null);
        if (contact == null) {
            contact = Contact.builder()
                    .user(user)
                    .type(ContactType.TELEGRAM)
                    .build();
        }
        contact.setContact(tgContactDto.getTgChatId());
        contactRepository.save(contact);
    }

    public ExtendedContactDto getUserContact(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user found by this id: " + userId));
        Contact contact = user.getContacts()
                .stream()
                .filter(c -> c.getType().equals(ContactType.TELEGRAM))
                .findFirst()
                .orElse(null);
        ExtendedContactDto tgContact = ExtendedContactDto
                .builder()
                .userId(userId)
                .username(user.getUsername())
                .phone(user.getPhone())
                .tgChatId(contact != null ? contact.getContact() : null)
                .build();
        return tgContact;
    }


    public Long findUserIdByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhone(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("No user found by this phone: " + phoneNumber)).getId();
    }
}
