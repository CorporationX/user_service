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
        return getMentorById(mentorId).getMentees().stream()
                .map(userMapper::toUserDTO)
                .toList();
    }

    public List<UserDTO> getMentorsOfUser(long menteeId) {
        return getMenteeById(menteeId).getMentors().stream()
                .map(userMapper::toUserDTO)
                .toList();
    }

    public User getMentorById(long mentorId) {
        return userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor with id " + mentorId + " not found"));
    }

    public User getMenteeById(long menteeId) {
        return userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee were not found for this id " + menteeId));
    }
}
