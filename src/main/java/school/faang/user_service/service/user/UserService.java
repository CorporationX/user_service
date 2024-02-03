package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public User findUserById(long userId) {
        return userRepo.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("User not found"));
    }
}
