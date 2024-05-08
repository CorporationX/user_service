package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
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
        return userMapper.toDtoList(user.getMentors());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long userId) {
        User user = getUserById(userId);
        return userMapper.toDtoList(user.getMentees());
    }

    @Transactional
    public void deleteMentee(long mentorId, long menteeId) {
        User user = getUserById(mentorId);
        if (mentorId == menteeId) {
            throw new ValidationException("Same mentorId and menteeId");
        }
        user.getMentees().removeIf(mentee -> mentee.getId() == menteeId);
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        User user = getUserById(menteeId);
        if (mentorId == menteeId) {
            throw new ValidationException("Same mentorId and menteeId");
        }
        user.getMentors().removeIf(mentor -> mentor.getId() == mentorId);
    }

    // Утилитный private метод
    private User getUserById(long id) {
        return mentorshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
