package school.faang.user_service.service.user;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

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
                .orElseThrow(() -> new DataValidationException("Incorrect user id"));

        removeGoals(userToDeactivate);
        removeEvents(userToDeactivate);
        userToDeactivate.setActive(false);
        stopMentorship(userToDeactivate);

        userRepository.save(userToDeactivate);
    }

    @Override
    @Transactional
    public void addUsersFromFile(InputStream fileStream) throws IOException {
        List<Person> persons = getPersonsFromFile(fileStream);
        persons.stream().map(student -> {
            var user = userMapper.personToUser(student);
            user.setPassword(generatePassword());
            countryRepository.findByTitle(student.getContactInfo().getAddress().getCountry())
                    .ifPresentOrElse(user::setCountry,
                            () -> {
                               var country = countryRepository.save(
                                        Country.builder()
                                                .title(student.getContactInfo().getAddress().getCountry())
                                                .build()
                                );
                               user.setCountry(country);
                            }
                    );
            userRepository.save(user);
            return user;
        }).toList();
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalStateException(
                "User with ID: %d does not exist.".formatted(userId)));
    }

    private void stopMentorship(User userToDeactivate) {
        mentorshipService.deleteMentorFromMentees(userToDeactivate.getId(), userToDeactivate.getMentees());
    }

    private void removeEvents(User userToDeactivate) {
        List<Event> eventsToRemove = userToDeactivate.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .toList();

        eventService.deleteEvents(eventsToRemove);
    }

    private void removeGoals(User userToDeactivate) {
        List<Goal> goalsToRemove = userToDeactivate.getGoals()
                .stream()
                .peek(goal -> goal.getUsers().removeIf(user -> user.getId().equals(userToDeactivate.getId())))
                .filter(goal -> goal.getUsers().isEmpty())
                .toList();

        goalService.removeGoals(goalsToRemove);
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

    private List<Person> getPersonsFromFile(InputStream fileStream) throws IOException {
        return new CsvMapper()
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader())
                .<Person>readValues(fileStream)
                .readAll();
    }
}
