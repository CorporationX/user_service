package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserAsyncService userAsyncService;
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
            ListUtils.partition(students, partitionSize).forEach(userAsyncService::mapAndSaveStudents);
        } else {
            userAsyncService.mapAndSaveStudents(students);
        }
    }
}
