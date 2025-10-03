package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class MentorshipServiceImpl implements MentorshipService {

    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    private MentorshipPair authorizeAndLoadUsers(long mentorId, long menteeId) {
        long currentUserId = userContext.getUserId();

        if (currentUserId != mentorId && currentUserId != menteeId) {
            throw new ForbiddenException("Доступ запрещен");
        }

        if (mentorId == menteeId) {
            throw new DataValidationException("Вы не можете выбрать себя");
        }

        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        return new MentorshipPair(mentor, mentee);
    }

    @Override
    public void addMentorship(long mentorId, long menteeId) {
        MentorshipPair pair = authorizeAndLoadUsers(mentorId, menteeId);
        pair.mentee.getMentors().add(pair.mentor);
        mentorshipRepository.save(pair.mentee);
    }

    @Override
    public void deleteMentorship(long mentorId, long menteeId) {
        MentorshipPair pair = authorizeAndLoadUsers(mentorId, menteeId);
        pair.mentee.getMentors().remove(pair.mentor);
        mentorshipRepository.save(pair.mentee);
    }

    @Override
    public List<UserDto> getMentees(long userId) {
        User user = mentorshipRepository.getByIdOrThrow(userId);
        List<User> mentees = user.getMentees();

        if (mentees == null || mentees.isEmpty()) {
            return List.of();
        }

        return mentees.stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        User user = mentorshipRepository.getByIdOrThrow(userId);
        List<User> mentors = user.getMentors();

        if (mentors == null || mentors.isEmpty()) {
            return List.of();
        }

        return mentors.stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDto)
                .toList();
    }

    private record MentorshipPair(User mentor, User mentee) {}
}