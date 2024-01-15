package school.faang.user_service.controller.mentorship;

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
    private final UserMapper userMapper;

    public List<UserDTO> getMenteesOfUser(long mentorId) {
        return getUserById(mentorId).getMentees().stream()
                .map(userMapper::toUserDTO)
                .toList();
    }

    public List<UserDTO> getMentorsOfUser(long menteeId) {
        return getUserById(menteeId).getMentors().stream()
                .map(userMapper::toUserDTO)
                .toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = getUserById(menteeId);
        User mentor = getUserById(mentorId);
        if (mentor.getMentees().contains(mentee))
            mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUserById(menteeId);
        User mentor = getUserById(mentorId);
        if (mentee.getMentors().contains(mentor))
            mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    public User getUserById(long mentorId) {
        return userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + mentorId + " not found"));
    }
}
