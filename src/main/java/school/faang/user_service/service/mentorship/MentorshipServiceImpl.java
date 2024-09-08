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
        if (userId < 0) {
            throw new DataValidationException("id пользователя не может быть отрицательным");
        }
        User user = getUserById(userId);
        return userMapper.toMentorshipUserDtos(user.getMentees());
    }

    @Override
    public List<MentorshipUserDto> getMentors(Long userId) {
        if (userId < 0) {
            throw new DataValidationException("id пользователя не может быть отрицательным");
        }
        User user = getUserById(userId);
        return userMapper.toMentorshipUserDtos(user.getMentors());
    }

    @Override
    public void deleteMentorship(Long menteeId, Long mentorId) {
        if (mentorId < 0) {
            throw new DataValidationException("Ментор не может иметь отрицательный идентификатор %d"
                    .formatted(mentorId));
        }
        if (menteeId < 0) {
            throw new DataValidationException("Менти не может иметь отрицательный идентификатор %d"
                    .formatted(mentorId));
        }
        if (Objects.equals(menteeId, mentorId)) {
            throw new DataValidationException("Ментор и менти не могут иметь одинаковые идентификаторы");
        }
        User mentee = getUserById(menteeId);
        List<User> mentors = mentee.getMentors();
        boolean isRemoved = mentors.removeIf(mentor -> mentor.getId().equals(mentorId));
        if (isRemoved) {
            mentorshipRepository.save(mentee);
        } else {
            System.out.println("12312321");
            throw new EntityNotFoundException("Не найдена пара менти с id %d и ментора с id %d"
                    .formatted(menteeId, mentorId));
        }
    }

    private User getUserById(Long userId) {
        return mentorshipRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь с id %d".formatted(userId)));
    }
}
