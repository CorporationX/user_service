package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.pojo.Person;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PersonMapper personMapper;

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
        students.forEach(student -> {
            User user = personMapper.toUser(student);
            System.out.println(user.getUsername() + " " + user.getEmail() + " "
                    + user.getPhone() + " " + user.getCity() + " " + user.getCountry().getTitle() + " " + user.getAboutMe());
        });
    }
}
