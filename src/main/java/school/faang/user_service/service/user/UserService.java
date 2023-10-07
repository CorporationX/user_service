package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.ResponseDeactivateDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.parser.PersonParser;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.util.PasswordGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorsService;
    private final PasswordGenerator passwordGenerator;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;
    private final PersonParser personParser;
    private final Executor taskExecutor;
    @Value("${spring.students.partitionSize}")
    private int partitionSize;

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public void validateUsers(Long... userIds) {
        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User with id " + userId + " not found.");
            }
        }
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        return userMapper.toDto(getUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void saveStudents(MultipartFile studentsFile) {
        List<Person> students = personParser.parse(studentsFile);

        if (students.size() > partitionSize) {
            ListUtils.partition(students, partitionSize)
                    .forEach(partition -> taskExecutor.execute(() -> mapAndSaveStudents(partition)));
        } else {
            taskExecutor.execute(() -> mapAndSaveStudents(students));
        }
    }

    private void mapAndSaveStudents(List<Person> students) {
        List<User> users = students.stream()
                .map(personMapper::toUser)
                .peek(user -> {
                    user.setPassword(passwordGenerator.generatePassword());
                    countryRepository.findByTitle(user.getCountry().getTitle())
                            .ifPresentOrElse(
                                    user::setCountry,
                                    () -> user.setCountry(countryRepository.save(user.getCountry()))
                            );
                })
                .toList();

        log.info("Successfully mapped from Person and saved {} users", users.size());
        userRepository.saveAll(users);
    }

    @Transactional
    public ResponseDeactivateDto deactivateUser(Long userId) {
        User user = getUser(userId);
        ResponseDeactivateDto response = new ResponseDeactivateDto("", userId);
        if (!user.isActive()) {
            log.warn("User with id {} is already deactivated", userId);
            response.setMessage("User is already deactivated");
            return response;
        } else {
            cancelUserEvents(userId);
            removeUserGoals(userId);
            mentorsService.cancelMentorship(userId);

            user.setActive(false);
            response.setMessage("User was successfully deactivated");
        }
        return response;
    }


    private void removeUserGoals(long userId) {
        List<Goal> goals = new ArrayList<>(goalRepository.findGoalsByUserId(userId).toList());
        List<Goal> goalsToBeDeleted = goals.stream()
                .filter(goal -> goal.getUsers().size() == 1)
                .toList();
        goalRepository.deleteAll(goalsToBeDeleted);

        goals.removeAll(goalsToBeDeleted);
        goals.forEach(goal -> deleteUser(goal, userId));

        goalRepository.saveAll(goals);
    }

    private void cancelUserEvents(Long userId) {
        try {
            eventRepository.deleteByOwnerId(userId);
        } catch (Exception e) {
            log.error("Error occurred while canceling events for user with ID {}: {}", userId, e.getMessage());
        }
    }

    private void deleteUser(Goal goal, long userId) {
       goal.getUsers().removeIf(userGoal -> userGoal.getId() == (userId));
    }
}
