package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d not found", id)));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new school.faang.user_service.exception.EntityNotFoundException("User with id = " + userId + " is not exists"));
    }

    public boolean isOwnerExistById(Long id) {
        return userRepository.existsById(id);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new school.faang.user_service.exception.EntityNotFoundException("User with id = " + userId + " is not exists"));
    }

    public void saveUser(User savedUser) {
        if (isOwnerExistById(savedUser.getId())) {
            userRepository.save(savedUser);
        }
    }
}
