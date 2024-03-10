package school.faang.user_service.userService;

import school.faang.user_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void deleteMentee(long menteeId, long mentorId) {
        Optional<User> mentorOptional = userRepository.findById(mentorId);
        User mentor = mentorOptional.orElseThrow(() -> new IllegalArgumentException("Invalid mentor Id"));
        mentor.getMentees().remove(menteeId);
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        Optional<User> menteeOptional = userRepository.findById(menteeId);
        User mentee = menteeOptional.orElseThrow(() -> new IllegalArgumentException("Invalid mentee Id"));
        mentee.getMentors().remove(mentorId);
        userRepository.save(mentee);
    }
}
