package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
