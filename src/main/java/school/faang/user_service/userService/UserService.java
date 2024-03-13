package school.faang.user_service.userService;

import school.faang.user_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
        public User findUserById(Long userId) {
            if(userId < 1){
                throw new IllegalArgumentException("id is not found");
            }
            User user = userRepository.findById(userId).orElse(null);
            return user;
        }
    public User findMenteeById(Long menteeId) {
        if(menteeId < 1){
            throw new IllegalArgumentException("id is not found");
        }
        User user = userRepository.findById(menteeId).orElse(null);
        return user;
    }
    public User findMentorById(Long mentorId) {
        if(mentorId < 1){
            throw new IllegalArgumentException("id is not found");
        }
        User user = userRepository.findById(mentorId).orElse(null);
        return user;
    }
}

