package school.faang.user_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.jpa.UserJpaRepository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserJpaRepository userJpaRepository;

    public User findById(Long userId) {
        return userJpaRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User not found with id: %s", userId)));
    }
}
