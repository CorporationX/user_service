package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Controller
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;

    public List<User> getMentees(Long userId) {
        List<User> mentees = new ArrayList<>();
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchElementException("There is no user with such ID");
        } else {
            return userRepository.findById(userId).get().getMentees();
        }
    }
}

