package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorshipServiceImpl implements MentorshipService {

    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Override
    public void addMentorship(long mentorId, long menteeId) {
        log.info("Adding mentorship: mentorId={}, menteeId={}", mentorId, menteeId);

        MentorshipPair pair = authorizeAndLoadUsers(mentorId, menteeId);
        pair.mentee.getMentors().add(pair.mentor);
        mentorshipRepository.save(pair.mentee);

        log.info("Mentorship added successfully: mentorId={}, menteeId={}", mentorId, menteeId);
    }

    @Override
    public void deleteMentorship(long mentorId, long menteeId) {
        log.info("Deleting mentorship: mentorId={}, menteeId={}", mentorId, menteeId);

        MentorshipPair pair = authorizeAndLoadUsers(mentorId, menteeId);
        pair.mentee.getMentors().remove(pair.mentor);
        mentorshipRepository.save(pair.mentee);

        log.info("Mentorship deleted successfully: mentorId={}, menteeId={}", mentorId, menteeId);
    }

    @Override
    public List<UserDto> getMentees(long userId) {
        log.debug("Fetching mentees for userId={}", userId);

        User user = mentorshipRepository.getByIdOrThrow(userId);
        List<User> mentees = user.getMentees();

        if (mentees == null || mentees.isEmpty()) {
            log.debug("No mentees found for userId={}", userId);
            return List.of();
        }

        return mentees.stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        log.debug("Fetching mentors for userId={}", userId);

        User user = mentorshipRepository.getByIdOrThrow(userId);
        List<User> mentors = user.getMentors();

        if (mentors == null || mentors.isEmpty()) {
            log.debug("No mentors found for userId={}", userId);
            return List.of();
        }

        return mentors.stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDto)
                .toList();
    }

    private MentorshipPair authorizeAndLoadUsers(long mentorId, long menteeId) {
        long currentUserId = userContext.getUserId();

        if (currentUserId != mentorId && currentUserId != menteeId) {
            log.warn("Access denied for userId={} trying to manage mentorship between mentorId={} and menteeId={}",
                    currentUserId, mentorId, menteeId);
            throw new ForbiddenException("Доступ запрещен");
        }

        if (mentorId == menteeId) {
            log.warn("Invalid mentorship request: mentorId equals menteeId={}", mentorId);
            throw new DataValidationException("Вы не можете выбрать себя");
        }

        User mentor = mentorshipRepository.getByIdOrThrow(mentorId);
        User mentee = mentorshipRepository.getByIdOrThrow(menteeId);
        return new MentorshipPair(mentor, mentee);
    }

    private record MentorshipPair(User mentor, User mentee) {}
}