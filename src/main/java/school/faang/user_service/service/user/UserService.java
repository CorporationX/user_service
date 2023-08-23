package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.parser.PersonParser;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.PasswordGenerator;

import java.util.List;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordGenerator passwordGenerator;
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
                .orElseThrow(() ->  new EntityNotFoundException("User with id " + id + " not found"));
    }

    public void validateUsers(Long... userIds) {
        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User with id " + userId + " not found.");
            }
        }
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
}
