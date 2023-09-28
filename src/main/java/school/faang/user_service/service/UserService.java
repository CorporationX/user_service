package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.executors.ExecutorsPull;
import school.faang.user_service.csv_parser.CsvToPerson.CsvToPerson;
import school.faang.user_service.dto.DeactivateResponseDto;
import school.faang.user_service.dto.contact.ExtendedContactDto;
import school.faang.user_service.dto.contact.TgContactDto;
import school.faang.user_service.dto.redis.ProfileViewEventDto;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.dto.user.person_dto.UserPersonDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.exception.DeactivationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.ProfileViewPublisher;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserCheckRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.PasswordGeneration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final CsvToPerson csvToPerson;
    private final PersonToUserMapper personToUserMapper;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;
    private final UserCheckRepository userCheckRepository;
    private final PasswordGeneration passwordGeneration;
    private final ProfileViewPublisher profileViewPublisher;
    private final UserContext userContext;
    private final Object lock = new Object();
    private final ExecutorsPull executorsPull;

    @Value("${dicebear.url}")
    private String dicebearUrl;

    public UserDto getUserWithPublishProfileViewEvent(long id) throws JsonProcessingException {
        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            throw new UserNotFoundException("User with id " + id + " not found");
        });
        log.info("Return user with id: {}", foundUser.getId());
        long viewerId = userContext.getUserId();
        profileViewPublisher.publish(new ProfileViewEventDto(viewerId, id));
        return userMapper.toUserDto(foundUser);
    }

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

    @Transactional
    public void registerAnArrayOfUser(InputStream stream) {
        Map<String, Country> countryMap = new HashMap<>();
        countryRepository.findAll().forEach(country -> countryMap.put(country.getTitle(), country));
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String finalLine = line;
                    executorsPull.pullUserService().getPull().execute(() -> saveUserStudent(finalLine, countryMap));
                } catch (DataValidException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("recording error, repeat request");
        }
    }

    @Transactional
    public void saveUserStudent(String line, Map<String, Country> countryBD) {
        UserPersonDto personDto = csvToPerson.getPerson(line);
        User user = personToUserMapper.toUser(personDto);

        String personCountry = personDto.getContactInfo().getAddress().getCountry().strip();
        List<User> users = userCheckRepository.findDistinctPeopleByUsernameOrEmailOrPhone(personDto.getUsername(),
                personDto.getContactInfo().getEmail(), personDto.getContactInfo().getPhone());
        List<UserPersonDto> personDtos = users.stream().map(personToUserMapper::toUserPersonDto).toList();
        if (personDtos.contains(personDto)) {
            return;
        }

        user.setPassword(passwordGeneration.getPassword().get());

        checkingAndCreatingACountry(countryBD, personCountry, user);

        userRepository.save(user);
    }

    @Transactional
    public void checkingAndCreatingACountry(Map<String, Country> countryBD, String personCountry, User user) {
        if (countryBD.containsKey(personCountry)) {
            user.setCountry(countryBD.get(personCountry));
        } else {
            synchronized (lock) {
                Map<String, Country> countryMap = new HashMap<>();
                countryRepository.findAll().forEach(country -> countryMap.put(country.getTitle(), country));
                if (countryMap.containsKey(personCountry)) {
                    user.setCountry(countryMap.get(personCountry));
                } else {
                    Country countrySave = new Country();
                    countrySave.setTitle(personCountry.strip());
                    Country country = countryRepository.save(countrySave);
                    user.setCountry(country);
                }
            }
        }
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

    @Transactional(readOnly = true)
    public Boolean checkUserExist(long id) {
        return userRepository.existsById(id);
    }
}
