package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(long userId) {
        User user = getUserById(userId);
        return userMapper.toDto(user.getMentees());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long userId) {
        User user = getUserById(userId);
        return userMapper.toDto(user.getMentors());
    }

    @Transactional
    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);

        List<User> mentees = mentor.getMentees();

        if (mentees.isEmpty()) {
            throw new IllegalArgumentException(
                    "That mentee doesn't have any mentors"
            );
        } else if (mentees.contains(mentee)) {
            mentor.getMentees().removeIf(user -> user.getId() == menteeId);
            mentorshipRepository.save(mentor);
        } else {
            throw new IllegalArgumentException(
                    "That mentee: " + menteeId + " not available for this mentor."
            );
        }
    }

    @Transactional
    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = getUserById(menteeId);
        User mentor = getUserById(mentorId);

        List<User> mentors = mentee.getMentors();

        if (mentors.isEmpty()) {
            throw new IllegalArgumentException(
                    "That mentor doesn't have any mentees"
            );
        } else if (mentors.contains(mentor)) {
            mentee.getMentors().removeIf(user -> user.getId() == mentorId);
            mentorshipRepository.save(mentee);
        } else {
            throw new IllegalArgumentException(
                    "That mentor: " + mentorId + " not available for this mentee."
            );
        }
    }

    private User getUserById(Long id) {
        return mentorshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
