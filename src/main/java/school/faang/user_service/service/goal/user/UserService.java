package school.faang.user_service.service.goal.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAllById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
