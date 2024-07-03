package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;


    public List<User> getMentees(Long userId) {
        User mentor = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        return mentor.getMentees();

    }

    public List<User> getMentors(Long userId) {
        User mentee = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        return mentee.getMentors();
    }

    public void deleteMentee(Long menteeId, Long mentorId) {

        User mentor = userRepository.findById(mentorId).orElseThrow(RuntimeException::new);
        User mentee = mentor.getMentees()
                .stream()
                .filter(m -> m.getId() == menteeId)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        mentor.getMentees().remove(mentee);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = userRepository.findById(menteeId).orElseThrow(RuntimeException::new);
        User mentor = mentee.getMentors()
                .stream()
                .filter(m -> m.getId() ==mentorId)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        mentee.getMentors().remove(mentor);


    }
}
