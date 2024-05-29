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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(long userId) {
        User user = getUserById(userId);
        return userMapper.toDtoList(user.getMentees());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long userId) {
        User user = getUserById(userId);
        return userMapper.toDtoList(user.getMentors());
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);

        List<User> mentees = mentor.getMentees();

        if (mentees.contains(mentee)) {
            mentor.setMentees(mentees.
                    stream().
                    filter(user -> !user.equals(mentee)).
                    collect(Collectors.toList()));
            mentorshipRepository.save(mentor);
        } else {
            throw new IllegalArgumentException(
                    "That mentee: " + menteeId + " not available for this mentor."
            );
        }
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = getUserById(menteeId);
        User mentor = getUserById(mentorId);
        List<User> mentors = mentee.getMentors();
        if (mentors.contains(mentor)) {
            mentee.setMentors(mentors.
                    stream().
                    filter(user -> !user.equals(mentor)).
                    collect(Collectors.toList()));
            mentorshipRepository.save(mentee);
        } else {
            throw new IllegalArgumentException(
                    "That mentor: " + mentorId + " not available for this mentee."
            );
        }
    }

    // Утилитный private метод
    private User getUserById(Long id) {
        return mentorshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
