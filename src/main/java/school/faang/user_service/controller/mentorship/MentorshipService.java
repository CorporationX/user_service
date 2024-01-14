package school.faang.user_service.controller.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getMenteesOfUser(long mentorId) {
        User mentorById = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor with id " + mentorId + " not found"));

        return mentorById.getMentees().stream()
                .map(userMapper::toUserDTO)
                .toList();
    }

    public List<UserDTO> getMentorsOfUser(long menteeId) {
        User menteeById = userRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("Mentee were not found for this id " + menteeId));

        return menteeById.getMentors().stream()
                .map(userMapper::toUserDTO)
                .toList();
    }
}
