package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Override
    public List<MentorshipUserDto> getMentees(Long userId) {
        User user = getUserById(userId);
        return userMapper.toMentorshipUserDtoList(user.getMentees());
    }

    @Override
    public List<MentorshipUserDto> getMentors(Long userId) {
        User user = getUserById(userId);
        return userMapper.toMentorshipUserDtoList(user.getMentors());
    }

    @Override
    public void deleteMentee(Long menteeId, Long mentorId) {
        deleteMentorship(menteeId, mentorId);
    }

    @Override
    public void deleteMentor(Long menteeId, Long mentorId) {
        deleteMentorship(menteeId, mentorId);
    }

    private void deleteMentorship(Long menteeId, Long mentorId) {
        User mentee = getUserById(menteeId);
        List<User> mentors = mentee.getMentors();
        boolean isRemoved = mentors.removeIf(mentor -> mentor.getId().equals(mentorId));
        if (!isRemoved) {
            throw new MentorNotFoundException("У менти с id %d не найден ментор с id %d"
                    .formatted(menteeId, mentorId));
        }
        mentorshipRepository.save(mentee);
    }

    private User getUserById(Long userId) {
        return mentorshipRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Не найден пользователь с id %d".formatted(userId)));
    }
}
