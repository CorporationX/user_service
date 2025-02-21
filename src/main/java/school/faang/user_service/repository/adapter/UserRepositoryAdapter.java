package school.faang.user_service.repository.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter {
    private final UserRepository userRepository;

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public User getById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    public List<User> getAllById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }
}
