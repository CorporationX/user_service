package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter {
    private final UserRepository userRepository;

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }
}
