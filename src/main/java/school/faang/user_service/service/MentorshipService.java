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

    public void deleteMentee(Long menteeId, Long mentorId){
        User mentee = userRepository.findById(menteeId).orElseThrow(() ->
                new EntityNotFoundException("Invalid mentee ID"));
        User mentor = userRepository.findById(mentorId).orElseThrow(() ->
                new EntityNotFoundException("Invalid mentor ID"));

        if (mentor.getId() == mentee.getId()) {
            throw new IllegalArgumentException("Invalid deletion. You can't be mentee of yourself");
        }

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }
}

