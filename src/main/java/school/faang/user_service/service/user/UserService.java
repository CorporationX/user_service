package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.PasswordGenerator;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PersonMapper personMapper;
    private final PasswordGenerator passwordGenerator;
    private final TaskExecutor taskExecutor;
    @Value("${spring.students.partitionSize}")
    private int partitionSize;

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public void validateUsers(Long... userIds) {
        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User with id " + userId + " not found.");
            }
        }
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->  new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Transactional
    public void saveStudents(List<Person> students) {
        if (students.size() > partitionSize) {
            List<List<Person>> partitions = ListUtils.partition(students, students.size() / partitionSize);
            partitions.forEach(partition -> taskExecutor.execute(() -> mapAndSaveStudents(partition)));
        } else if (students.size() > 0) {
            mapAndSaveStudents(students);
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

        userRepository.saveAll(users);
    }
}
