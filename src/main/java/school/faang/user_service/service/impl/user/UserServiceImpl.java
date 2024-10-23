package school.faang.user_service.service.impl.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.student.Person;
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.Country;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.event.Event;
import school.faang.user_service.model.enums.event.EventStatus;
import school.faang.user_service.model.entity.goal.Goal;


import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.RecommendUserPublisher;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.util.CsvParser;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final CsvParser csvParser;

    @Override
    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        var premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(premiumUsers, (stream, filter) -> filter.apply(stream, filters),
                        (newStream, oldStream) -> newStream)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void deactivateUserProfile(long id) {
        User userToDeactivate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: %d does not exist"));

        removeGoals(userToDeactivate);
        removeEvents(userToDeactivate);
        userToDeactivate.setActive(false);
        stopMentorship(userToDeactivate);

        userRepository.save(userToDeactivate);
    }

    @Override
    @Transactional
    public void addUsersFromFile(InputStream fileStream) {
        List<Person> persons = csvParser.getPersonsFromFile(fileStream);
        List<Country> countryList = countryRepository.findAll();
        persons.stream().map(student -> {
            var user = userMapper.personToUser(student);
            user.setPassword(generatePassword());
            setStudentsCountry(student, user, countryList);
            userRepository.save(user);
            return user;
        }).forEach(v -> {
        });
    }

    private void setStudentsCountry(Person person, User user, List<Country> countryList) {
        Optional<Country> country = gerPersonsCountryFromDB(person, countryList);
        country.ifPresentOrElse(user::setCountry, () -> {
            var saved = countryRepository.save(
                    Country.builder()
                            .title(person.getContactInfo().getAddress().getCountry())
                            .build()
            );
            user.setCountry(saved);
        });
    }

    private Optional<Country> gerPersonsCountryFromDB(Person person, List<Country> countryList) {
        return countryList.stream().filter(country -> Objects.equals(person.getContactInfo().getAddress().getCountry(), country.getTitle())).findFirst();
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "User with ID: %d does not exist.".formatted(userId)));
    }

    private void stopMentorship(User userToDeactivate) { // Maybe move to mentorship service
        mentorshipService.deleteMentorFromMentees(userToDeactivate.getId(), userToDeactivate.getMentees());
    }

    private void removeEvents(User userToDeactivate) { // Move to eventService
        List<Event> eventsToRemove = userToDeactivate.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .toList();

        eventService.deleteEvents(eventsToRemove);
    }

    private void removeGoals(User userToDeactivate) { // Move to goalService
        List<Goal> goalsToRemove = userToDeactivate.getGoals()
                .stream()
                .peek(goal -> goal.getUsers().removeIf(user -> user.getId().equals(userToDeactivate.getId())))
                .filter(goal -> goal.getUsers().isEmpty())
                .toList();

        goalService.removeGoals(goalsToRemove);
    }

    @Override
    public UserDto getUser(long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void banUserById(Long id) {
        User userToBan = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        userToBan.setBanned(true);

        userRepository.save(userToBan);
        log.info("Banned user with ID {}", id);
    }

    private String generatePassword() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        var passwordLenth = 8;
        StringBuilder password = new StringBuilder(passwordLenth);
        for (int i = 0; i < passwordLenth; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
