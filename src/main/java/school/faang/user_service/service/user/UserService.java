package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.event.SearchAppearanceEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;
    private final CountryService countryService;
    private final PersonService personService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final CsvPersonParser csvPersonParser;
    private final UserProfilePic generatedUserProfilePic;
    private final List<UserFilter> userFilters;
    private final SearchAppearanceEventPublisher eventPublisher;

    public UserRegistrationDto createUser(UserRegistrationDto userDto) {
        User user = userMapper.toEntity(userDto);

        if (user.getUserProfilePic() == null) {
            user.setUserProfilePic(generatedUserProfilePic);
        }
        user.setCountry(countryService.getSavedCountry(user.getCountry()));

        User savedUser = userRepository.save(user);
        return userMapper.toRegDto(savedUser);
    }

    @Transactional
    public UserDto getUserDtoById(long id) {
        userValidator.validateAccessToUser(id);
        return userMapper.toDto(getUserById(id));
    }

    public UserProfilePic getUserPicUrlById(long id) {
        userValidator.validateAccessToUser(id);
        return getUserById(id).getUserProfilePic();
    }

    public void deactivationUserById(long userId) {
        User user = getUserById(userId);
        stopGoalsAndDeleteEventsAndDeleteMentor(user);

        user.setActive(false);
        saveUser(user);
    }

    @Transactional(readOnly = true)
    public boolean isOwnerExistById(Long id) {
        return userRepository.existsById(id);
    }

    public void saveUser(User savedUser) {
        if (isOwnerExistById(savedUser.getId())) {
            userRepository.save(savedUser);
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d not found", id)));
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        List<User> users = userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .flatMap(filter -> filter.apply(premiumUsers, userFilterDto)).toList();
        return userMapper.toDtoList(users);
    }

    private void stopGoalsAndDeleteEventsAndDeleteMentor(User user) {
        List<Goal> goals = user.getGoals();
        for (Goal goal : goals) {
            if (goal.getUsers().size() == 1) {
                goalRepository.deleteById(goal.getId());
            }
        }

        List<Event> events = user.getOwnedEvents();
        for (Event event : events) {
            eventRepository.deleteById(event.getId());
        }

        List<User> mentees = user.getMentees();
        for (User mentee : mentees) {
            List<User> menteeMentors = mentee.getMentors();
            User mentor = getUserById(user.getId());
            if (menteeMentors.remove(mentor)) {
                mentorshipRepository.save(mentee);
            }
        }
    }

    public void saveStudents(MultipartFile csvFile) {
        List<Person> people;
        try {
            people = csvPersonParser.parse(csvFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        personService.savePeopleAsUsers(people);
        log.info("Students saved from csv file as users. Saved accounts count: {}", people.size());
    }

    public List<UserDto> getUsers(UserFilterDto filter, long actorId) {
        List<User> users = userRepository.findAll();

        userFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(users.stream(), filter));

        users.forEach(user -> {
            SearchAppearanceEventDto event = new SearchAppearanceEventDto(
                    user.getId(),
                    actorId,
                    LocalDateTime.now()
            );
            eventPublisher.publish(event);
        });

        return new ArrayList<>(users.stream()
                .map(userMapper::toDto).toList());
    }
}