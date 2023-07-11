package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;

    public List<User> getMentees(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("There is no user with such ID");
        } else {
            return userRepository.findById(userId).get().getMentees();
        }
    }

    public List<User> getMentors(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("There is no user with such ID");
        } else {
            return userRepository.findById(userId).get().getMentors();
        }
    }
}

