package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Override
    public List<MentorshipUserDto> getMentees(Long userId) {
        validateNegativeId(userId);
        User user = getUserById(userId);
        return userMapper.toMentorshipUserDtos(user.getMentees());
    }

    @Override
    public List<MentorshipUserDto> getMentors(Long userId) {
        validateNegativeId(userId);
        User user = getUserById(userId);
        return userMapper.toMentorshipUserDtos(user.getMentors());
    }

    @Override
    public void deleteMentorship(Long menteeId, Long mentorId) {
        validateNegativeId(menteeId);
        validateNegativeId(mentorId);
        if (Objects.equals(menteeId, mentorId)) {
            throw new DataValidationException("Mentor and mentee must not have the same ID: %d"
                    .formatted(menteeId));
        }
        User mentee = getUserById(menteeId);
        List<User> mentors = mentee.getMentors();
        boolean isRemoved = mentors.removeIf(mentor -> mentor.getId().equals(mentorId));
        if (isRemoved) {
            mentorshipRepository.save(mentee);
        } else {
            throw new EntityNotFoundException("A pair of mentee with id %d and a mentor with id %d was not found"
                    .formatted(menteeId, mentorId));
        }
    }

    private User getUserById(Long userId) {
        return mentorshipRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
    }

    private void validateNegativeId(Long id) {
        if (id < 0) {
            throw new DataValidationException("ID has incorrect value: %d"
                    .formatted(id));
        }
    }
}
