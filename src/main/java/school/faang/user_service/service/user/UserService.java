package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Person;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private static final int USER_PASSWORD_LENGTH = 10;
    private static final int BATCH_SIZE = 100;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final CountryRepository countryRepository;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;

    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));
        return userMapper.toDto(user);
    }

    private boolean shouldGoalBeDeleted(GoalDto goal, Long userId) {
        List<Long> userIds = goal.getUserIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }

    private boolean shouldEventBeDeleted(EventDto event, Long userId) {
        // If event owner is not a deactivated user
        if (!Objects.equals(event.getOwnerId(), userId)) {
            return false;
        }

        List<Long> userIds = event.getAttendeesIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }

    private void stopUserGoals(Long userId) {
        List<Long> userGoalsForDeleting = new ArrayList<>();
        List<Long> userGoalsForUpdating = new ArrayList<>();

        List<GoalDto> allGoals = goalService.getGoalsByUser(userId);

        for (GoalDto goal : allGoals) {
            if (shouldGoalBeDeleted(goal, userId)) {
                userGoalsForDeleting.add(goal.getId());
            } else {
                userGoalsForUpdating.add(goal.getId());
            }
        }

        goalService.deleteAllByIds(userGoalsForDeleting);
        goalService.removeUserFromGoals(userGoalsForUpdating, userId);
    }

    private void stopUserEvents(Long userId) {
        List<Long> userEventsForDeleting = new ArrayList<>();
        List<Long> userEventsForUpdating = new ArrayList<>();

        List<EventDto> allevents = eventService.getParticipatedEvents(userId);

        for (EventDto event : allevents) {
            if (shouldEventBeDeleted(event, userId)) {
                userEventsForDeleting.add(event.getId());
            } else {
                userEventsForUpdating.add(event.getId());
            }
        }

        eventService.deleteAllByIds(userEventsForDeleting);
        eventService.removeUserFromEvents(userEventsForUpdating, userId);
    }

    private void cancelMentoring(Long userId) {
        mentorshipService.cancelMentoring(userId);
    }


    public void deactivateUser(Long userId) {
        stopUserGoals(userId);
        stopUserEvents(userId);
        cancelMentoring(userId);
    }

    public void processCsv(InputStream inputStream) {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numProcessors);
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try {
            MappingIterator<Person> iterator = csvMapper.readerFor(Person.class)
                    .with(schema)
                    .readValues(inputStream);

            List<Person> studentBatch = new ArrayList<>();
            while (iterator.hasNext()) {
                studentBatch.add(iterator.next());
                if (studentBatch.size() >= BATCH_SIZE) {
                    List<Person> finalStudentBatch = studentBatch;
                    executorService.submit(() -> processBatch(finalStudentBatch));
                    studentBatch = new ArrayList<>();
                }
            }
            if (!studentBatch.isEmpty()) {
                processBatch(studentBatch);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    private void processBatch(List<Person> miniBatch) {
        log.info(Thread.currentThread().getName());
        List<User> users = new ArrayList<>();
        for (Person person : miniBatch) {
            ContactInfo contactInfo = new ContactInfo();
            Address address = new Address();
            contactInfo.setEmail((String) person.getAdditionalProperties().get("email"));
            contactInfo.setPhone((String) person.getAdditionalProperties().get("phone"));
            address.setCountry((String) person.getAdditionalProperties().get("city"));
            address.setCity((String) person.getAdditionalProperties().get("country"));
            contactInfo.setAddress(address);
            person.setContactInfo(contactInfo);
            users.add(personMapper.personToUser(person));
        }
        saveUsers(users);
    }

    private void saveUsers(List<User> users) {
        for (User user : users) {
            String countryTitle = user.getCountry().getTitle();
            user.setUsername(user.getEmail());
            user.setPassword(generatePassword());
            Country country = findCountryByTitle(countryTitle);
            user.setCountry(country);
        }
        batchInsertUsers(users);
    }

    private void batchInsertUsers(List<User> users) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                entityManager.persist(user);

                if (i % BATCH_SIZE == 0 || i == users.size() - 1) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private Country findCountryByTitle(@NotNull String title) {
        return countryRepository.findByTitle(title)
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setTitle(title);
                    return countryRepository.save(newCountry);
                });
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(UserService.USER_PASSWORD_LENGTH);
    }
}
