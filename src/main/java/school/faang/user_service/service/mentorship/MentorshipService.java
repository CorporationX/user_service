package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(long mentorId) {
        User user = getUserById(mentorId);
        return user.getMentees().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long userId) {
        User user = getUserById(userId);
        return user.getMentors().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        User mentor = getUserById(mentorId);
        mentor.getMentees().removeIf(mentee -> mentee.getId() == menteeId);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getUserById(menteeId);
        mentee.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
    }

    private User getUserById(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}

